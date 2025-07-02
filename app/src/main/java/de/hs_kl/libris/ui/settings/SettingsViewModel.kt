package de.hs_kl.libris.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hs_kl.libris.data.model.ReadingStatus
import de.hs_kl.libris.data.repository.BookRepository
import de.hs_kl.libris.util.getDisplayName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: BookRepository,
) : ViewModel() {

    private val _statisticsText =
        MutableStateFlow("Total books: 0\nNot Started: 0\nIn Progress: 0\nCompleted: 0\nOn Hold: 0\nDropped: 0\n")
    val statisticsText = _statisticsText.asStateFlow()

    fun observeStatistics(context: Context) {
        viewModelScope.launch {
            repository.getAllBooks().collect { books ->
                val bookCount = books.size
                val statsBuilder = StringBuilder()

                statsBuilder.append("Total books: $bookCount\n")

                for (status in ReadingStatus.entries) {
                    val count = books.count { it.status == status }
                    statsBuilder.append("${status.getDisplayName(context)}: $count\n")
                }

                _statisticsText.value = statsBuilder.toString()
            }
        }
    }


    fun populateTestData() {
        viewModelScope.launch {
            repository.populateTestData()
        }
    }

    fun clearLibrary() {
        viewModelScope.launch {
            repository.clearAllBooks()
        }
    }
}