package de.hs_kl.libris.data.api

import de.hs_kl.libris.data.model.BookSearchResult

interface BookSearchService {
    suspend fun searchBooks(
        query: String,
        filters: BookSearchFilters = BookSearchFilters()
    ): Result<List<BookSearchResult>>

    val serviceName: String
}

data class BookSearchFilters(
    val author: String? = null,
    val language: String? = null,
    val category: String? = null,
    val publisher: String? = null,
    val isbn: String? = null,
    val startIndex: Int = 0,
    val maxResults: Int = 20
)