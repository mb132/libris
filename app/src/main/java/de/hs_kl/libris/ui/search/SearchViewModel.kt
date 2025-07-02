package de.hs_kl.libris.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hs_kl.libris.data.api.BookSearchFilters
import de.hs_kl.libris.data.model.BookSearchResult
import de.hs_kl.libris.data.repository.BookRepository
import de.hs_kl.libris.data.repository.BookSearchRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: BookSearchRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Active filters
    private val _activeFilters = MutableStateFlow(BookSearchFilters())
    val activeFilters = _activeFilters.asStateFlow()

    // Pagination state
    private var currentPage = 0
    private var canLoadMore = true
    private var isSearching = false

    // Search results
    private val _searchResults = MutableStateFlow<List<SearchResultUiModel>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    // scroll debounce
    private var lastScrollTime = 0L
    private val SCROLL_DEBOUNCE_TIME = 500L // 500ms debounce

    // search debounce
    @OptIn(FlowPreview::class)
    private val debouncedQuery = searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    init {
        viewModelScope.launch {
            debouncedQuery.collect { query ->
                if (query.isNotBlank()) {
                    resetAndSearch()
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(type: FilterType, value: String) {
        _activeFilters.value = when (type) {
            FilterType.LANGUAGE -> _activeFilters.value.copy(language = value)
            FilterType.GENRE -> _activeFilters.value.copy(category = value)
            FilterType.AUTHOR -> _activeFilters.value.copy(author = value)
        }
        if (type == FilterType.GENRE || type == FilterType.AUTHOR) {
            viewModelScope.launch {
                resetAndSearch()
            }
        }
        // Language filter should only trigger search when others are set / query is not blank
        if (type == FilterType.LANGUAGE && (_activeFilters.value.category != null ||
                _activeFilters.value.author != null || _searchQuery.value.isNotBlank())
        ) {
            viewModelScope.launch {
                resetAndSearch()
            }
        }

    }

    fun clearFilter(type: FilterType) {
        _activeFilters.value = when (type) {
            FilterType.LANGUAGE -> _activeFilters.value.copy(language = null)
            FilterType.GENRE -> _activeFilters.value.copy(category = null)
            FilterType.AUTHOR -> _activeFilters.value.copy(author = null)
        }
        viewModelScope.launch {
            resetAndSearch()
        }
    }

    fun loadMoreResults() {
        val currentTime = System.currentTimeMillis()
        if (!canLoadMore || isSearching ||
            currentTime - lastScrollTime < SCROLL_DEBOUNCE_TIME
        ) return

        lastScrollTime = currentTime

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)

                // Add delay between pagination requests
                delay(300)

                val nextPage = currentPage + 1
                val filters = _activeFilters.value.copy(
                    startIndex = nextPage * 20,
                    maxResults = 20
                )

                searchRepository.searchBooks(_searchQuery.value, filters).collect { result ->
                    result.fold(
                        onSuccess = { books ->
                            if (books.isEmpty()) {
                                canLoadMore = false
                            } else {
                                currentPage = nextPage
                                val newResults = books.map { it.toUiModel() }
                                _searchResults.value += newResults
                            }
                        },
                        onFailure = { error ->
                            if (error.message?.contains("503") == true) {
                                delay(1000) // Wait longer on 503 errors
                                loadMoreResults() // Retry
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    error = "Failed to load more results: ${error.message}"
                                )
                            }
                        }
                    )
                }
            } finally {
                _uiState.value = _uiState.value.copy(isLoadingMore = false)
            }
        }
    }

    private fun resetAndSearch() {
        viewModelScope.launch {
            currentPage = 0
            canLoadMore = true
            _searchResults.value = emptyList()
            search()
        }
    }

    private suspend fun search() {
        // should allow search with blank query if filters are active
        val hasActiveFilters = with(_activeFilters.value) {
            !author.isNullOrBlank() || !category.isNullOrBlank() || !language.isNullOrBlank()
        }
        println("hasActiveFilters: $hasActiveFilters")
        println("searchQuery: ${_searchQuery.value}")
        // Skip search if query is blank and no filters are active
        if (_searchQuery.value.isBlank() && !hasActiveFilters) return


        try {
            isSearching = true
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val filters = _activeFilters.value.copy(
                startIndex = 0,
                maxResults = 20
            )

            val result = searchRepository.searchBooksFromAllSources(_searchQuery.value, filters)
            when {
                result.isSuccess -> {
                    val books = result.getOrNull() ?: emptyList()
                    if (books.isEmpty()) {
                        canLoadMore = false
                        _uiState.value = _uiState.value.copy(
                            error = "No results found"
                        )
                    } else {
                        val results = books.map { it.toUiModel() }
                        _searchResults.value = results
                    }
                }

                result.isFailure -> {
                    val error = result.exceptionOrNull()
                    _uiState.value = _uiState.value.copy(
                        error = "Search failed: ${error?.message}"
                    )
                }
            }
        } finally {
            isSearching = false
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private suspend fun BookSearchResult.toUiModel(): SearchResultUiModel {
        val existingBook = bookRepository.getBookByApiRef(id, source)
        return SearchResultUiModel(
            id = id,
            title = title,
            authors = authors,
            description = description,
            isbn = isbn,
            pageCount = pageCount,
            categories = categories,
            language = language,
            publisher = publisher,
            publishedDate = publishedDate,
            coverUrl = coverUrl,
            source = source,
            isInLibrary = existingBook != null,
            bookId = existingBook?.id
        )
    }

    fun addToLibrary(result: SearchResultUiModel) {
        viewModelScope.launch {
            try {
                bookRepository.addBook(result.toBook())
                _searchResults.value = _searchResults.value.map {
                    if (it.id == result.id && it.source == result.source) {
                        it.copy(isInLibrary = true)
                    } else it
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to add book: ${e.message}"
                )
            }
        }
    }

    fun updateBookAddedStatus(bookId: String) {
        viewModelScope.launch {
            val book = bookRepository.getBookById(bookId) ?: return@launch

            // update the search results to show the book is in library
            _searchResults.value = _searchResults.value.map { result ->
                if (result.id == book.apiId && result.source == book.apiSource) {
                    result.copy(
                        isInLibrary = true,
                        bookId = bookId
                    )
                } else result
            }
        }
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)

enum class FilterType {
    LANGUAGE,
    GENRE,
    AUTHOR
}

