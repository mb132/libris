package de.hs_kl.libris.data.repository

import de.hs_kl.libris.data.api.BookSearchFilters
import de.hs_kl.libris.data.api.BookSearchService
import de.hs_kl.libris.data.model.BookSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookSearchRepository @Inject constructor(
    private val searchServices: @JvmSuppressWildcards List<BookSearchService>
) {

    fun searchBooks(
        query: String,
        filters: BookSearchFilters = BookSearchFilters()
    ): Flow<Result<List<BookSearchResult>>> = flow {
        // futureproofing, since currently we only have one service implemented
        for (service in searchServices) {
            println("Trying service: ${service.javaClass.simpleName}")
            val result = service.searchBooks(query, filters)
            if (result.isSuccess) {
                emit(result)
                break
            }
            // send last failure, if all fail
            if (service == searchServices.last()) {
                emit(result)
            }
        }
    }

    suspend fun searchBooksFromAllSources(
        query: String,
        filters: BookSearchFilters = BookSearchFilters()
    ): Result<List<BookSearchResult>> = try {
        val allResults = searchServices.mapNotNull { service ->
            service.searchBooks(query, filters).getOrNull()
        }.flatten()

        val uniqueResults = allResults.distinctBy { result ->
            result.isbn ?: "${result.title}${result.authors.joinToString()}"
        }

        Result.success(uniqueResults)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
