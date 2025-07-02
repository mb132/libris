package de.hs_kl.libris.ui.library

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import de.hs_kl.libris.R
import de.hs_kl.libris.data.model.ReadingStatus
import de.hs_kl.libris.databinding.FragmentLibraryBinding
import de.hs_kl.libris.util.getDisplayName
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupPager()
        setupMenuAndSearch()
        setupFabAddBook()
        observeViewModel()
    }

    private fun setupFabAddBook() {
        binding.fabAddBook.setOnClickListener {
            findNavController().navigate(
                LibraryFragmentDirections.actionNavigationLibraryToBookDetail(
                    bookId = null,
                    isNew = true
                )
            )
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> {
                    viewModel.setSearchMode(true)
                    true
                }

                R.id.action_sort -> {
                    showSortMenu()
                    true
                }

                else -> false
            }
        }
    }

    private fun setupMenuAndSearch() {
        binding.searchToolbar.setNavigationOnClickListener {
            viewModel.setSearchMode(false)
        }

        binding.searchInput.addTextChangedListener {
            viewModel.updateSearchQuery(it?.toString() ?: "")
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                true
            } else false
        }
    }

    private fun showSortMenu() {
        val toolbar = binding.toolbar
        val popup = PopupMenu(requireContext(), toolbar.findViewById(R.id.action_sort))
        popup.menuInflater.inflate(R.menu.menu_sort, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sort_title_asc -> {
                    viewModel.updateSortOption(SortOption.TITLE_ASC)
                    true
                }

                R.id.sort_title_desc -> {
                    viewModel.updateSortOption(SortOption.TITLE_DESC)
                    true
                }

                R.id.sort_author -> {
                    viewModel.updateSortOption(SortOption.AUTHOR)
                    true
                }

                R.id.sort_recent -> {
                    viewModel.updateSortOption(SortOption.RECENTLY_ADDED)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun setupPager() {
        val pagerAdapter = LibraryPagerAdapter(this)
        binding.pager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            val status = ReadingStatus.entries[position]
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.tabCounts.collect { counts ->
                    val count = counts[status] ?: 0
                    tab.text = "${status.getDisplayName(requireContext())} ($count)"
                }
            }
        }.attach()

        binding.pager.isUserInputEnabled = true
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSearchMode.collect { isSearchMode ->
                updateSearchMode(isSearchMode)
            }
        }
    }

    private fun updateSearchMode(isSearchMode: Boolean) {
        binding.toolbar.isVisible = !isSearchMode
        binding.searchToolbar.isVisible = isSearchMode

        if (isSearchMode) {
            binding.searchInput.requestFocus()
            showKeyboard(binding.searchInput)
        } else {
            binding.searchInput.text = null
            hideKeyboard()
        }
    }

    private fun showKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view?.windowToken?.let { token ->
            imm.hideSoftInputFromWindow(token, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
