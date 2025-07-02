package de.hs_kl.libris.data.api

import de.hs_kl.libris.data.model.BookSearchResult
import de.hs_kl.libris.util.LanguageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject


class GoogleBooksService @Inject constructor(
    private val client: OkHttpClient = OkHttpClient(),
    private val languageManager: LanguageManager
) : BookSearchService {

    companion object {
        private const val BASE_URL = "https://www.googleapis.com/books/v1/volumes"
        private const val MAX_RESULTS = 20

        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    override val serviceName: String = "Google Books"

    override suspend fun searchBooks(
        query: String,
        filters: BookSearchFilters
    ): Result<List<BookSearchResult>> {

        return try {
            withContext(Dispatchers.IO) {
                val url = buildSearchUrl(query, filters)
                val request = Request.Builder().url(url).build()

                println("Google Books API request: $url")

                val response = client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("API call failed: ${response.code} - ${response.message}")
                    }
                    response.body.string()
                }

                val searchResponse = json.decodeFromString<GoogleBooksResponse>(response)
                println("Google Books search results: ${searchResponse.items?.size}")

                Result.success(searchResponse.items
                    ?.filter { it.volumeInfo.title != null }
                    ?.map { it.toBookSearchResult(languageManager) }
                    ?: emptyList())
            }
        } catch (e: Exception) {
            println("Google Books search failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun buildSearchUrl(query: String, filters: BookSearchFilters): String {
        val queryParams = mutableListOf<String>()

        val searchTerms = mutableListOf<String>()
        searchTerms.add(query)

        filters.author?.let { searchTerms.add("inauthor:$it") }
        filters.publisher?.let { searchTerms.add("inpublisher:$it") }
        filters.isbn?.let { searchTerms.add("isbn:$it") }
        filters.category?.let { searchTerms.add("subject:$it") }
        queryParams.add("q=${searchTerms.joinToString(" ").encodeUrl()}")

        filters.language?.let { displayName ->
            languageManager.getISOCode(displayName)?.let { isoCode ->
                queryParams.add("langRestrict=$isoCode")
            }
        }

        queryParams.add("maxResults=${filters.maxResults.coerceAtMost(MAX_RESULTS)}")
        queryParams.add("startIndex=${filters.startIndex}")

        return "$BASE_URL?${queryParams.joinToString("&")}"
    }

    private fun String.encodeUrl(): String =
        java.net.URLEncoder.encode(this, "UTF-8")

    @Serializable
    private data class GoogleBooksResponse(
        val items: List<GoogleBookItem>? = null,
        val totalItems: Int = 0
    )

    @Serializable
    private data class GoogleBookItem(
        val id: String,
        val volumeInfo: VolumeInfo
    ) {
        fun toBookSearchResult(langManager: LanguageManager) = BookSearchResult(
            id = id,
            title = volumeInfo.title ?: "Unknown Title",
            authors = volumeInfo.authors ?: listOf("Unknown Author"),
            description = volumeInfo.description,
            isbn = volumeInfo.industryIdentifiers?.firstOrNull { it.type == "ISBN_13" }?.identifier,
            pageCount = volumeInfo.pageCount ?: 0,
            categories = volumeInfo.categories ?: emptyList(),
            language = langManager.getDisplayName(volumeInfo.language ?: "") ?: volumeInfo.language,
            publisher = volumeInfo.publisher,
            publishedDate = volumeInfo.publishedDate,
            coverUrl = volumeInfo.imageLinks?.thumbnail?.replace("zoom=1", "zoom=2"),
            source = "Google Books"
        )
    }

    @Serializable
    private data class VolumeInfo(
        val title: String? = null,
        val authors: List<String>? = null,
        val publisher: String? = null,
        val publishedDate: String? = null,
        val description: String? = null,
        val industryIdentifiers: List<IndustryIdentifier>? = null,
        val pageCount: Int? = null,
        val categories: List<String>? = null,
        val imageLinks: ImageLinks? = null,
        val language: String? = null
    )

    @Serializable
    private data class IndustryIdentifier(
        val type: String,
        val identifier: String
    )

    @Serializable
    private data class ImageLinks(
        val thumbnail: String? = null
    )
}


