package de.hs_kl.libris.data.model

data class BookSearchResult(
    val id: String, // API ID
    val title: String,
    val authors: List<String>,
    val description: String?,
    val isbn: String?,
    val pageCount: Int,
    val categories: List<String>,
    val language: String?,
    val publisher: String?,
    val publishedDate: String?,
    val coverUrl: String?,
    val source: String
)