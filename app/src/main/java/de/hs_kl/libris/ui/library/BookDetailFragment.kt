package de.hs_kl.libris.ui.library

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import de.hs_kl.libris.R
import de.hs_kl.libris.data.model.Book
import de.hs_kl.libris.data.model.ReadingStatus
import de.hs_kl.libris.databinding.FragmentBookDetailBinding
import de.hs_kl.libris.ui.components.PageInputDialog
import de.hs_kl.libris.util.ImageLoader
import de.hs_kl.libris.util.getDisplayName
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class BookDetailFragment : Fragment() {
    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookDetailViewModel by viewModels()
    private val args: BookDetailFragmentArgs by navArgs()

    private val textWatchers = mutableMapOf<TextInputEditText, TextWatcher>()
    private var isFromSearch = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isFromSearch = args.searchResult != null && args.bookId == null

        setupToolbar()
        observeViewModel()
        setupQuickProgressUpdate()

        when {
            args.searchResult != null -> {
                viewModel.loadBook(
                    bookId = null,
                    isNew = false, // otherwise we launch in edit mode
                    searchResult = args.searchResult
                )
            }

            args.bookId != null -> {
                viewModel.loadBook(
                    bookId = args.bookId,
                    isNew = false,
                    searchResult = null
                )
            }

            else -> {
                viewModel.loadBook(
                    bookId = null,
                    isNew = true,
                    searchResult = null
                )
            }
        }
    }

    private fun setupQuickProgressUpdate() {
        binding.fabUpdateProgress.setOnClickListener {
            viewModel.book.value?.let { book ->
                PageInputDialog.newInstance(
                    currentPage = book.currentPage,
                    maxPage = book.pageCount,
                    onPageSet = { newPage ->
                        if (isFromSearch && !viewModel.isInLibrary()) {
                            lifecycleScope.launch {
                                viewModel.addToLibrary()

                                updateBookProgress(book, newPage)
                            }
                        } else {
                            updateBookProgress(book, newPage)
                        }
                    }
                ).show(childFragmentManager, "page_input_dialog")
            }
        }
    }

    private fun updateBookProgress(book: Book, newPage: Int) {
        val newStatus = when {
            newPage == book.pageCount -> ReadingStatus.COMPLETED
            newPage > 0 -> ReadingStatus.IN_PROGRESS
            else -> book.status
        }

        viewModel.updateField("currentPage", newPage.toString())
        if (newStatus != book.status) {
            viewModel.updateField("status", newStatus)
        }

        viewModel.saveChanges()
    }


    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            inflateMenu(R.menu.menu_book_detail)

            menu.findItem(R.id.action_add_to_library)?.isVisible = isFromSearch

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_add_to_library -> {
                        viewModel.addToLibrary()
                        true
                    }

                    R.id.action_edit -> {
                        viewModel.enableEditMode()
                        true
                    }

                    R.id.action_save -> {
                        viewModel.saveChanges()
                        true
                    }

                    R.id.action_cancel -> {
                        viewModel.cancelEdit()
                        true
                    }

                    R.id.action_delete -> {
                        showDeleteConfirmation()
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun updateToolbarMenu(isEditing: Boolean) {
        binding.toolbar.menu.apply {
            findItem(R.id.action_edit)?.isVisible = !isEditing && !isFromSearch
            findItem(R.id.action_save)?.isVisible = isEditing
            findItem(R.id.action_cancel)?.isVisible = isEditing
            findItem(R.id.action_delete)?.isVisible = !isEditing && !isFromSearch
            findItem(R.id.action_add_to_library)?.isVisible =
                isFromSearch && !viewModel.isInLibrary() && !isEditing
        }
    }


    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_book_title)
            .setMessage(R.string.delete_book_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteBook()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setupEditableFields() {
        clearTextWatchers()

        binding.apply {
            // Text fields
            setupTextField(titleInput.editText, "title")
            setupTextField(authorInput.editText, "author")
            setupTextField(isbnInput.editText, "isbn")
            setupTextField(currentPageInput.editText, "currentPage")
            setupTextField(totalPagesInput.editText, "totalPages")
            setupTextField(publisherInput.editText, "publisher")
            setupTextField(publishedDateInput.editText, "publishedDate")
            setupTextField(startDateInput.editText, "status")
            setupTextField(completionDateInput.editText, "completionDate")
            setupTextField(descriptionInput.editText, "description")

            // Date fields
            startDateInput.editText?.setOnClickListener {
                showDatePicker(BookDetailViewModel.DatePickerTarget.START_DATE)
            }
            completionDateInput.editText?.setOnClickListener {
                showDatePicker(BookDetailViewModel.DatePickerTarget.COMPLETION_DATE)
            }
            publishedDateInput.editText?.setOnClickListener {
                showDatePicker(BookDetailViewModel.DatePickerTarget.PUBLICATION_DATE)
            }

            // Setup the categories input field with auto-completion
            binding.categoryChips.setOnCategoryChangeListener { categories ->
                viewModel.updateField("categories", categories.joinToString(","))
            }

            setupDropdowns()

            changeCoverButton.setOnClickListener {
                launchImagePicker()
            }
        }
    }


    private fun setupTextField(editText: EditText?, field: String) {
        editText?.let { et ->
            var isUpdatingProgrammatically = false

            val watcher = object : TextWatcher {
                private var beforeText: String? = null

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    if (!isUpdatingProgrammatically) {
                        beforeText = s?.toString()
                    }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!isUpdatingProgrammatically && beforeText != s?.toString()) {
                        viewModel.updateFieldDebounced(field, s?.toString())
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // reset after programmatic update
                    isUpdatingProgrammatically = false
                }
            }
            et.addTextChangedListener(watcher)
            (et as? TextInputEditText)?.let {
                textWatchers[it] = watcher
            }
        }
    }

    private fun setupDropdowns() {
        binding.apply {
            statusInput.editText?.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    setupStatusAdapter(v as? AutoCompleteTextView)
                }
            }

            languageInput.editText?.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    setupLanguageAdapter(v as? AutoCompleteTextView)
                }
            }
        }
    }

    private fun setupStatusAdapter(autoCompleteTextView: AutoCompleteTextView?) {
        autoCompleteTextView?.let { actv ->
            if (actv.adapter == null) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    ReadingStatus.entries.map { it.getDisplayName(requireContext()) }
                )
                actv.setAdapter(adapter)
                actv.setOnItemClickListener { _, _, position, _ ->
                    viewModel.updateField("status", ReadingStatus.entries[position])
                }
            }
        }
    }

    private fun setupLanguageAdapter(autoCompleteTextView: AutoCompleteTextView?) {
        autoCompleteTextView?.let { actv ->
            if (actv.adapter == null) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    resources.getStringArray(R.array.languages)
                )
                actv.setAdapter(adapter)
                actv.setOnItemClickListener { _, _, position, _ ->
                    val languages = resources.getStringArray(R.array.languages)
                    viewModel.updateField("language", languages[position])
                }
            }
        }
    }

    private fun clearTextWatchers() {
        textWatchers.forEach { (editText, watcher) ->
            editText.removeTextChangedListener(watcher)
        }
        textWatchers.clear()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.book.collect { book ->
                book?.let { updateBookDisplay(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editState.collect { state ->
                updateEditMode(state)
                handleValidationErrors(state.validationErrors)
                updateToolbarMenu(state.isEditing)
                binding.fabUpdateProgress.isVisible = !state.isEditing
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { event ->
                handleEvent(event)

                // handle book added to library event
                if (event is BookDetailEvent.AddedToLibrary) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "added_book_id",
                        event.bookId
                    )
                }
            }
        }

        // add observer for isInLibrary state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isInLibrary.collect { isInLibrary ->
                binding.toolbar.menu.findItem(R.id.action_add_to_library)?.isVisible =
                    isFromSearch && !isInLibrary
            }
        }
    }

    private fun updateBookDisplay(book: Book) {
        binding.apply {
            loadCoverImage(book)
            titleText.text = book.title
            authorText.text = book.author
            isbnText.text = buildStyledText(
                getString(R.string.isbn_label),
                book.isbn ?: getString(R.string.no_publisher)
            )
            statusText.text = buildStyledText(
                getString(R.string.status_label),
                book.status.getDisplayName(requireContext())
            )
            progressText.text = buildStyledText(
                getString(R.string.pages_label),
                "${book.currentPage}/${book.pageCount}"
            )
            publisherText.text = buildStyledText(
                getString(R.string.publisher_label),
                book.publisher ?: getString(R.string.no_publisher)
            )
            languageText.text = buildStyledText(
                getString(R.string.language_label),
                book.language ?: getString(R.string.no_language)
            )
            categoriesText.text = buildStyledText(
                getString(R.string.categories_label),
                if (book.categories.isNotEmpty()) book.categories.joinToString(", ")
                else getString(R.string.no_categories)
            )
            categoryChips.setCategories(book.categories)
            descriptionText.text = book.description ?: getString(R.string.no_description_available)

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            startDateText.text = buildStyledText(
                getString(R.string.started_label),
                book.startDate?.let { dateFormat.format(it) } ?: getString(R.string.not_started)
            )

            completionDateText.text = buildStyledText(
                getString(R.string.completed_label),
                book.completionDate?.let { dateFormat.format(it) }
                    ?: getString(R.string.not_completed)
            )

            publishedDateText.text = buildStyledText(
                getString(R.string.published_date_format),
                book.publishedDate ?: getString(R.string.no_published_date)
            )

        }
    }

    private fun buildStyledText(label: String, value: String): SpannableString {
        val text = SpannableString("$label$value")
        text.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            label.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return text
    }

    private fun loadCoverImage(book: Book) {
        ImageLoader.loadBookCover(book, binding.coverImage, enableTransition = true)
    }

    private fun updateEditMode(state: BookDetailViewModel.EditState) {
        val isEditing = state.isEditing

        binding.apply {
            // Batch visibility updates
            listOf(
                titleText to titleInput,
                authorText to authorInput,
                isbnText to isbnInput,
                statusText to statusInput,
                progressText to progressInputContainer,
                publisherText to publisherInput,
                publishedDateText to publishedDateInput,
                languageText to languageInput,
                descriptionText to descriptionInput,
                startDateText to startDateInput,
                completionDateText to completionDateInput,
            ).forEach { (textView, inputLayout) ->
                textView.isVisible = !isEditing
                inputLayout.isVisible = isEditing
            }

            changeCoverButton.isVisible = isEditing

            categoriesText.isVisible = !isEditing
            categoryChips.isVisible = isEditing
            categoryChips.setEditMode(isEditing)

            if (isEditing) {
                state.editedBook?.let { book ->
                    populateFieldsWithoutWatchers(book)
                    categoryChips.setCategories(book.categories)
                }
                setupEditableFields()
            } else {
                clearTextWatchers()
            }
        }
    }

    private fun populateFieldsWithoutWatchers(book: Book) {
        clearTextWatchers()

        binding.apply {
            setTextIfDifferent(titleInput.editText, book.title)
            setTextIfDifferent(authorInput.editText, book.author)
            setTextIfDifferent(isbnInput.editText, book.isbn)
            setTextIfDifferent(currentPageInput.editText, book.currentPage.toString())
            setTextIfDifferent(totalPagesInput.editText, book.pageCount.toString())
            setTextIfDifferent(publisherInput.editText, book.publisher)
            setTextIfDifferent(publishedDateInput.editText, book.publishedDate)
            setTextIfDifferent(descriptionInput.editText, book.description)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            setTextIfDifferent(
                startDateInput.editText,
                book.startDate?.let { dateFormat.format(it) })
            setTextIfDifferent(
                completionDateInput.editText,
                book.completionDate?.let { dateFormat.format(it) })


            (statusInput.editText as? AutoCompleteTextView)?.setText(
                book.status.getDisplayName(
                    requireContext()
                ), false
            )
            (languageInput.editText as? AutoCompleteTextView)?.setText(book.language, false)

        }
    }

    private fun setTextIfDifferent(editText: EditText?, newText: String?) {
        editText?.let { et ->
            val currentText = et.text.toString()
            if (currentText != newText) {
                // store cursor position if this is a MultiAutoCompleteTextView
                val isMultiAutoComplete = et is MultiAutoCompleteTextView
                val cursor = if (isMultiAutoComplete) et.selectionStart else -1

                et.setText(newText)

                // restore cursor for MultiAutoCompleteTextView
                if (isMultiAutoComplete && cursor >= 0) {
                    val newPosition = minOf(cursor, newText?.length ?: 0)
                    et.setSelection(newPosition)
                }
            }
        }
    }

    private fun handleValidationErrors(errors: Map<String, String>) {
        binding.apply {
            titleInput.error = errors["title"]
            authorInput.error = errors["author"]
            currentPageInput.error = errors["currentPage"]
            totalPagesInput.error = errors["totalPages"]
            isbnInput.error = errors["isbn"]
        }
    }

    private fun handleEvent(event: BookDetailEvent) {
        when (event) {
            is BookDetailEvent.SaveSuccess -> {
                showSnackbar(getString(R.string.changes_saved))
            }

            is BookDetailEvent.SaveError -> {
                showSnackbar(getString(R.string.save_error, event.message))
            }

            is BookDetailEvent.ValidationError -> {
                showSnackbar(getString(R.string.validation_error))
            }

            is BookDetailEvent.DeleteSuccess -> {
                showSnackbar(getString(R.string.book_deleted))
                findNavController().navigateUp()
            }

            is BookDetailEvent.DeleteError -> {
                showSnackbar(getString(R.string.delete_error, event.message))
            }

            is BookDetailEvent.AddedToLibrary -> {
                showSnackbar(getString(R.string.book_added_to_library))
                // update the search results via navigation result
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    "added_book_id",
                    event.bookId
                )
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showDatePicker(target: BookDetailViewModel.DatePickerTarget) {
        val currentDate = when (target) {
            BookDetailViewModel.DatePickerTarget.START_DATE -> viewModel.editState.value.editedBook?.startDate
            BookDetailViewModel.DatePickerTarget.COMPLETION_DATE -> viewModel.editState.value.editedBook?.completionDate
            BookDetailViewModel.DatePickerTarget.PUBLICATION_DATE -> null // Publication date is stored as string
        } ?: Calendar.getInstance().time

        val calendar = Calendar.getInstance().apply {
            time = currentDate
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                when (target) {
                    BookDetailViewModel.DatePickerTarget.START_DATE ->
                        viewModel.updateField("startDate", calendar.time)

                    BookDetailViewModel.DatePickerTarget.COMPLETION_DATE ->
                        viewModel.updateField("completionDate", calendar.time)

                    BookDetailViewModel.DatePickerTarget.PUBLICATION_DATE -> {
                        // For publication date, store as formatted string
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        viewModel.updateField("publishedDate", dateFormat.format(calendar.time))
                    }
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updateCoverImage(it) }
    }

    private fun launchImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    override fun onDestroyView() {
        clearTextWatchers()
        _binding = null
        super.onDestroyView()
    }
}
