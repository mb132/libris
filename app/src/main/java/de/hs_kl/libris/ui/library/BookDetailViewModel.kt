package de.hs_kl.libris.ui.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hs_kl.libris.data.model.Book
import de.hs_kl.libris.data.model.ReadingStatus
import de.hs_kl.libris.data.repository.BookRepository
import de.hs_kl.libris.ui.search.SearchResultUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    data class EditState(
        val isEditing: Boolean = false,
        val originalBook: Book? = null,
        val editedBook: Book? = null,
        val validationErrors: Map<String, String> = emptyMap(),
        val isSaving: Boolean = false,
        val datePickerTarget: DatePickerTarget? = null,
        val isNewBook: Boolean = false,
        val isDeleting: Boolean = false
    )

    enum class DatePickerTarget {
        START_DATE,
        COMPLETION_DATE,
        PUBLICATION_DATE
    }

    private val _editState = MutableStateFlow(EditState())
    val editState = _editState.asStateFlow()

    private val _book = MutableStateFlow<Book?>(null)
    val book = _book.asStateFlow()

    private val _events = Channel<BookDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var _isInLibrary = MutableStateFlow(false)
    val isInLibrary = _isInLibrary.asStateFlow()

    fun isInLibrary(): Boolean = _isInLibrary.value

    // Debouncer for text field updates
    private val updateDebouncer = MutableStateFlow<Pair<String, Any?>?>(null)

    init {
        viewModelScope.launch {
            updateDebouncer
                .filterNotNull()
                .debounce(300)
                .collect { (field, value) ->
                    updateField(field, value)
                }
        }
    }

    fun loadBook(
        bookId: String?,
        isNew: Boolean = false,
        searchResult: SearchResultUiModel? = null
    ) {
        viewModelScope.launch {
            try {
                val book = when {
                    bookId != null -> repository.getBookById(bookId)
                    searchResult != null -> {
                        // check if book already exists in library
                        val existingBook = searchResult.id?.let { apiId ->
                            repository.getBookByApiRef(apiId, searchResult.source)
                        }
                        existingBook ?: searchResult.toBook()
                    }

                    else -> createNewBook()
                }

                book?.let { loadedBook ->
                    _book.value = loadedBook
                    _isInLibrary.value = bookId != null || (searchResult?.id?.let {
                        repository.doesBookExist(it, searchResult.source)
                    } ?: false)
                    _editState.update { state ->
                        state.copy(
                            originalBook = loadedBook,
                            editedBook = loadedBook,
                            isEditing = isNew,
                            isNewBook = isNew || (!_isInLibrary.value && searchResult != null)
                        )
                    }
                }
            } catch (e: Exception) {
                _events.send(BookDetailEvent.SaveError("Failed to load book: ${e.message}"))
            }
        }
    }

    fun deleteBook() {
        viewModelScope.launch {
            try {
                _editState.update { it.copy(isDeleting = true) }

                val book = _book.value ?: return@launch
                repository.deleteBook(book)

                _events.send(BookDetailEvent.DeleteSuccess)
            } catch (e: Exception) {
                _events.send(BookDetailEvent.DeleteError(e.message ?: "Unknown error"))
            } finally {
                _editState.update { it.copy(isDeleting = false) }
            }
        }
    }

    fun addToLibrary() {
        viewModelScope.launch {
            try {
                val currentBook = _editState.value.editedBook ?: return@launch

                repository.addBook(currentBook)

                _isInLibrary.value = true

                _book.value = currentBook
                _editState.update { state ->
                    state.copy(
                        originalBook = currentBook,
                        editedBook = currentBook,
                        isNewBook = false
                    )
                }

                _events.send(BookDetailEvent.AddedToLibrary(currentBook.id))

                _events.send(BookDetailEvent.SaveSuccess)
            } catch (e: Exception) {
                _events.send(BookDetailEvent.SaveError(e.message ?: "Unknown error"))
            }
        }
    }

    fun enableEditMode() {
        viewModelScope.launch {
            _editState.update { currentState ->
                currentState.copy(
                    isEditing = true,
                    editedBook = _book.value
                )
            }
        }
    }

    fun cancelEdit() {
        viewModelScope.launch {
            _editState.update { currentState ->
                currentState.copy(
                    isEditing = false,
                    editedBook = currentState.originalBook,
                    validationErrors = emptyMap()
                )
            }
            _book.value = _editState.value.originalBook
        }
    }

    fun updateFieldDebounced(field: String, value: Any?) {
        viewModelScope.launch {
            updateDebouncer.value = field to value
        }
    }

    fun updateField(field: String, value: Any?) {
        viewModelScope.launch {
            val currentBook = _editState.value.editedBook ?: return@launch

            val needsValidation = when (field) {
                "currentPage", "totalPages" -> true
                "startDate", "completionDate" -> true
                else -> false
            }

            val updatedBook = updateBookField(currentBook, field, value)

            // validate dates if either start or completion date is updated
            val validationResult = when {
                needsValidation -> validateBook(updatedBook)
                field in listOf("startDate", "completionDate") -> validateDates(updatedBook)
                else -> _editState.value.validationErrors
            }

            _editState.update { state ->
                state.copy(
                    editedBook = updatedBook,
                    validationErrors = validationResult
                )
            }

            if (field in listOf("startDate", "completionDate", "publishedDate", "status")) {
                _book.value = updatedBook
            }
        }
    }

    private fun updateBookField(book: Book, field: String, value: Any?): Book {
        return when (field) {
            "title" -> book.copy(title = value as? String ?: book.title)
            "author" -> book.copy(author = value as? String ?: book.author)
            "isbn" -> book.copy(isbn = value as? String)
            "currentPage" -> book.copy(
                currentPage = (value as? String)?.toIntOrNull() ?: book.currentPage,
                status = when {
                    (value as? String)?.toIntOrNull() == book.pageCount -> ReadingStatus.COMPLETED
                    (value as? String)?.toIntOrNull() ?: 0 > 0 -> ReadingStatus.IN_PROGRESS
                    else -> book.status
                }
            )

            "totalPages" -> book.copy(
                pageCount = (value as? String)?.toIntOrNull() ?: book.pageCount
            )

            "status" -> book.copy(
                status = value as? ReadingStatus ?: book.status,
                // update dates based on status
                startDate = when (value) {
                    ReadingStatus.IN_PROGRESS -> book.startDate ?: Date()
                    ReadingStatus.COMPLETED -> book.startDate
                    else -> book.startDate
                },
                completionDate = when (value) {
                    ReadingStatus.COMPLETED -> book.completionDate ?: Date()
                    else -> null
                }
            )

            "startDate" -> book.copy(
                startDate = value as? Date,
                status = when {
                    book.completionDate != null -> ReadingStatus.COMPLETED
                    value != null -> ReadingStatus.IN_PROGRESS
                    else -> book.status
                }
            )

            "completionDate" -> book.copy(
                completionDate = value as? Date,
                status = if (value != null) ReadingStatus.COMPLETED else book.status
            )

            "publisher" -> book.copy(publisher = value as? String)
            "publishedDate" -> book.copy(publishedDate = value as? String)
            "description" -> book.copy(description = value as? String)
            "language" -> book.copy(language = value as? String)
            "categories" -> book.copy(
                categories = (value as? String)
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?: book.categories
            )

            else -> book
        }
    }

    private fun validateDates(book: Book): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Validate start and completion dates
        if (book.completionDate != null && book.startDate != null) {
            if (book.completionDate.before(book.startDate)) {
                errors["completionDate"] = "Completion date cannot be before start date"
            }
        }

        // If completed status, require both dates
        if (book.status == ReadingStatus.COMPLETED) {
            if (book.startDate == null) {
                errors["startDate"] = "Start date is required for completed books"
            }
            if (book.completionDate == null) {
                errors["completionDate"] = "Completion date is required for completed books"
            }
        }

        return errors
    }

    fun saveChanges() {
        val currentState = _editState.value
        val editedBook = currentState.editedBook ?: return

        viewModelScope.launch {
            val errors = validateBook(editedBook)
            if (errors.isNotEmpty()) {
                _editState.update { it.copy(validationErrors = errors) }
                _events.send(BookDetailEvent.ValidationError)
                return@launch // targets this specific coroutine
            }

            try {
                _editState.update { it.copy(isSaving = true) }


                println("Saving book: ${editedBook.title}")
                if (currentState.isNewBook) {
                    println("Adding new book")
                    repository.addBook(editedBook)
                } else {
                    println("Updating existing book")
                    repository.updateBook(editedBook)
                }

                _book.value = editedBook
                _editState.update { state ->
                    state.copy(
                        isEditing = false,
                        isSaving = false,
                        originalBook = editedBook,
                        editedBook = editedBook,
                        validationErrors = emptyMap()
                    )
                }
                _events.send(BookDetailEvent.SaveSuccess)
            } catch (e: Exception) {
                _events.send(BookDetailEvent.SaveError(e.message ?: "Unknown error"))
                _editState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun updateCoverImage(uri: Uri) {
        val currentState = _editState.value
        val editedBook = currentState.editedBook ?: return

        viewModelScope.launch {
            try {
                println("Starting cover image update for book: ${editedBook.id}")
                println("Original cover path: ${editedBook.localCoverPath}")
                println("Original cover URL: ${editedBook.coverUrl}")

                val updatedBook = repository.updateBookCover(editedBook, uri)

                println("Cover update successful")
                println("New cover path: ${updatedBook.localCoverPath}")

                if (updatedBook.localCoverPath == null) {
                    throw IllegalStateException("Cover update failed - no local path returned")
                }

                _book.value = updatedBook
                _editState.update { state ->
                    state.copy(
                        editedBook = updatedBook,
                        originalBook = updatedBook
                    )
                }
                _events.send(BookDetailEvent.SaveSuccess)

            } catch (e: Exception) {
                println("Error in cover update: ${e.message}")
                e.printStackTrace()
                _events.send(BookDetailEvent.SaveError("Failed to update cover image: ${e.message}"))
            }
        }
    }

    private fun validateBook(book: Book): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (book.title.isBlank()) {
            errors["title"] = "Title cannot be empty"
        }

        if (book.author.isBlank()) {
            errors["author"] = "Author cannot be empty"
        }

        if (book.pageCount < 0) {
            errors["totalPages"] = "Page count cannot be negative"
        }

        if (book.currentPage > book.pageCount) {
            errors["currentPage"] = "Current page cannot exceed total pages"
        }

        if (book.currentPage < 0) {
            errors["currentPage"] = "Current page cannot be negative"
        }

        book.isbn?.let { isbn ->
            if (isbn.isNotBlank() && !isValidIsbn(isbn)) {
                errors["isbn"] = "Invalid ISBN format"
            }
        }

        return errors
    }

    private fun createNewBook(): Book {
        return Book(
            id = UUID.randomUUID().toString(),
            title = "",
            author = "",
            lastModified = Date()
        )
    }

    private fun isValidIsbn(isbn: String): Boolean {
        // very basic ISBN validation
        val digits = isbn.replace("-", "").replace(" ", "")
        return when (digits.length) {
            10 -> isValidIsbn10(digits)
            13 -> isValidIsbn13(digits)
            else -> false
        }
    }

    private fun isValidIsbn10(isbn: String): Boolean {
        if (!isbn.matches(Regex("^\\d{9}[\\d|X]$"))) return false

        var sum = 0
        for (i in 0..8) {
            sum += (isbn[i] - '0') * (10 - i)
        }

        val lastChar = isbn.last()
        sum += if (lastChar == 'X') 10 else lastChar - '0'

        return sum % 11 == 0
    }

    private fun isValidIsbn13(isbn: String): Boolean {
        if (!isbn.matches(Regex("^\\d{13}$"))) return false

        var sum = 0
        for (i in 0..11) {
            sum += (isbn[i] - '0') * (if (i % 2 == 0) 1 else 3)
        }

        val checkDigit = (10 - (sum % 10)) % 10
        return checkDigit == isbn.last() - '0'
    }
}

sealed class BookDetailEvent {
    data object SaveSuccess : BookDetailEvent()
    data class SaveError(val message: String) : BookDetailEvent()
    data object ValidationError : BookDetailEvent()
    data object DeleteSuccess : BookDetailEvent()
    data class DeleteError(val message: String) : BookDetailEvent()
    data class AddedToLibrary(val bookId: String) : BookDetailEvent()
}
