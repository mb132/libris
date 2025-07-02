package de.hs_kl.libris.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val isbn: String? = null,
    val pageCount: Int = 0,
    val currentPage: Int = 0,
    val coverUrl: String? = null,
    val localCoverPath: String? = null,
    val status: ReadingStatus = ReadingStatus.NOT_STARTED,
    val startDate: Date? = null,
    val completionDate: Date? = null,
    val lastModified: Date = Date(),
    val publisher: String? = null,
    val publishedDate: String? = null, // String because API is not consistent.
    val description: String? = null,
    @TypeConverters(Converters::class)
    val categories: List<String> = emptyList(),
    val language: String? = null,
    val apiId: String? = null,        // The ID from the API (e.g. Google Books ID)
    val apiSource: String? = null,    // The source of the book (e.g. "Google Books") Should be null for local books.
)


