package de.hs_kl.libris.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import de.hs_kl.libris.R
import de.hs_kl.libris.data.api.BookSearchFilters
import de.hs_kl.libris.databinding.FragmentSearchBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("added_book_id")
            ?.observe(viewLifecycleOwner) { bookId ->
                viewModel.updateBookAddedStatus(bookId)
            }


        setupRecyclerView()
        setupSearchInput()
        setupFilters()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchResultAdapter(
            onBookClick = { result ->
                // navigate based on whether book exists in library
                if (result.isInLibrary && result.bookId != null) {
                    findNavController().navigate(
                        SearchFragmentDirections.actionSearchToBookDetail(
                            bookId = result.bookId,
                            isNew = false,
                            searchResult = null
                        )
                    )
                } else {
                    findNavController().navigate(
                        SearchFragmentDirections.actionSearchToBookDetail(
                            bookId = null,
                            isNew = true,
                            searchResult = result
                        )
                    )
                }
            },
            onAddToLibrary = { result ->
                viewModel.addToLibrary(result)
            }
        )

        binding.searchResults.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)

            // scroll listener for pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                        && firstVisibleItemPosition >= 0
                    ) {
                        viewModel.loadMoreResults()
                    }
                }
            })
        }
    }

    private fun setupSearchInput() {
        binding.searchInput.apply {
            addTextChangedListener(onTextChanged = { text, _, _, _ ->
                viewModel.updateSearchQuery(text?.toString() ?: "")
            })

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.updateSearchQuery(binding.searchInput.text.toString())
                    true
                } else false
            }
        }
    }

    private fun updateFilterChips(filters: BookSearchFilters) {
        binding.apply {
            languageChip.apply {
                isChecked = !filters.language.isNullOrBlank()
                text = if (isChecked) {
                    getString(R.string.filter_language_selected, filters.language)
                } else {
                    getString(R.string.filter_language)
                }
            }

            genreChip.apply {
                isChecked = !filters.category.isNullOrBlank()
                text = if (isChecked) {
                    getString(R.string.filter_genre_selected, filters.category)
                } else {
                    getString(R.string.filter_genre)
                }
            }

            authorChip.apply {
                isChecked = !filters.author.isNullOrBlank()
                text = if (isChecked) {
                    getString(R.string.filter_author_selected, filters.author)
                } else {
                    getString(R.string.filter_author)
                }
            }
        }
    }

    private fun setupFilters() {
        binding.apply {
            // Prevent automatic checking when clicked
            languageChip.setOnCheckedChangeListener { chip, isChecked ->
                // Only allow changes from our Flow collector
                if (chip.isPressed) {
                    chip.isChecked = viewModel.activeFilters.value.language != null
                }
            }
            genreChip.setOnCheckedChangeListener { chip, isChecked ->
                if (chip.isPressed) {
                    chip.isChecked = viewModel.activeFilters.value.category != null
                }
            }
            authorChip.setOnCheckedChangeListener { chip, isChecked ->
                if (chip.isPressed) {
                    chip.isChecked = viewModel.activeFilters.value.author != null
                }
            }

            languageChip.setOnClickListener { showLanguageFilter() }
            genreChip.setOnClickListener { showGenreFilter() }
            authorChip.setOnClickListener { showAuthorFilter() }

            // Observe active filters and update chip states
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.activeFilters.collect { filters ->
                    languageChip.isChecked = !filters.language.isNullOrBlank()
                    genreChip.isChecked = !filters.category.isNullOrBlank()
                    authorChip.isChecked = !filters.author.isNullOrBlank()
                }
            }
        }
    }

    private fun showLanguageFilter() {
        val languages = resources.getStringArray(R.array.languages)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Language")
            .setItems(languages) { _, index ->
                viewModel.updateFilter(FilterType.LANGUAGE, languages[index])
            }
            .setNegativeButton("Clear") { _, _ ->
                viewModel.clearFilter(FilterType.LANGUAGE)
            }.setOnDismissListener {
                // should be handeled by collector
            }
            .show()
    }

    private fun showGenreFilter() {
        val genres = resources.getStringArray(R.array.book_genres)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Genre")
            .setItems(genres) { _, index ->
                viewModel.updateFilter(FilterType.GENRE, genres[index])
            }
            .setNegativeButton("Clear") { _, _ ->
                viewModel.clearFilter(FilterType.GENRE)
            }.setOnDismissListener {
                // should be handeled by collector
            }
            .show()
    }

    private fun showAuthorFilter() {
        val layout = layoutInflater.inflate(R.layout.dialog_author_filter, null)
        val input = layout.findViewById<EditText>(R.id.authorInput)

        // Pre-fill with current author if exists
        viewModel.activeFilters.value.author?.let {
            input.setText(it)
            input.setSelection(it.length)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Enter Author Name")
            .setView(layout)
            .setPositiveButton("Apply") { _, _ ->
                viewModel.updateFilter(FilterType.AUTHOR, input.text.toString())
            }
            .setNegativeButton("Clear") { _, _ ->
                viewModel.clearFilter(FilterType.AUTHOR)
            }.setOnDismissListener {
                // should be handeled by collector
            }
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                updateUI(state)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collectLatest { results ->
                searchAdapter.submitList(results)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeFilters.collect { filters ->
                updateFilterChips(filters)
            }
        }
    }

    private fun updateUI(state: SearchUiState) {
        binding.apply {
            progressIndicator.isVisible = state.isLoading
            loadingMore.isVisible = state.isLoadingMore

            state.error?.let { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}