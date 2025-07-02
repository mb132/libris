package de.hs_kl.libris.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hs_kl.libris.data.model.ReadingStatus
import de.hs_kl.libris.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    private val _isSearchMode = MutableStateFlow(false)
    val isSearchMode = _isSearchMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _currentSortOption = MutableStateFlow(SortOption.TITLE_ASC)
    val currentSortOption = _currentSortOption.asStateFlow()

    // Books stream/flow from repository
    private val _books = repository.getAllBooks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // function to get filtered and sorted books for a specific status
    fun getBooksForStatus(status: ReadingStatus) = combine(
        _books,
        searchQuery,
        currentSortOption
    ) { books, query, sortOption ->
        books.filter { book ->
            book.status == status &&
                (query.isEmpty() ||
                    book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true))
        }.let { filtered ->
            when (sortOption) {
                SortOption.TITLE_ASC -> filtered.sortedBy { it.title }
                SortOption.TITLE_DESC -> filtered.sortedByDescending { it.title }
                SortOption.AUTHOR -> filtered.sortedBy { it.author }
                SortOption.RECENTLY_ADDED -> filtered.sortedByDescending { it.lastModified }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Tab counts based on current search
    val tabCounts = combine(_books, searchQuery) { books, query ->
        val filtered = if (query.isEmpty()) books else {
            books.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.author.contains(query, ignoreCase = true)
            }
        }
        ReadingStatus.entries.associateWith { status ->
            filtered.count { it.status == status }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    fun setSearchMode(enabled: Boolean) {
        _isSearchMode.value = enabled
        if (!enabled) {
            _searchQuery.value = ""
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortOption(option: SortOption) {
        _currentSortOption.value = option
    }
}

enum class SortOption {
    TITLE_ASC,
    TITLE_DESC,
    AUTHOR,
    RECENTLY_ADDED
}