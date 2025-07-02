package de.hs_kl.libris.data.repository

import android.net.Uri
import de.hs_kl.libris.data.dao.BookDao
import de.hs_kl.libris.data.model.Book
import de.hs_kl.libris.data.model.ReadingStatus
import de.hs_kl.libris.data.storage.ImageStorage
import de.hs_kl.libris.util.TestData
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val imageStorage: ImageStorage
) {
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    fun getBooksByStatus(status: ReadingStatus): Flow<List<Book>> =
        bookDao.getBooksByStatus(status)

    suspend fun addBook(book: Book) {
        if (book.apiId != null && book.apiSource != null && bookDao.existsByApiId(
                book.apiId,
                book.apiSource
            )
        ) {
            println("Book already exists: ${book.apiId}, ${book.apiSource}")
            return
        }

        if (book.localCoverPath != null) {
            bookDao.insertBook(book)
            return
        }
        val localPath = book.coverUrl?.let { url ->
            imageStorage.saveImage(url, book.id)
        }

        bookDao.insertBook(book.copy(localCoverPath = localPath))
    }


    suspend fun updateBook(book: Book) {
        val existingBook = bookDao.getBookById(book.id)
        if (existingBook?.coverUrl != book.coverUrl) {
            existingBook?.localCoverPath?.let {
                imageStorage.deleteImage(book.id)
            }

            val localPath = book.coverUrl?.let { url ->
                imageStorage.saveImage(url, book.id)
            }
            bookDao.updateBook(book.copy(localCoverPath = localPath))
        } else {
            bookDao.updateBook(book)
        }
    }

    suspend fun updateBookCover(book: Book, coverUri: Uri): Book {
        try {
            println("Starting cover update for book: ${book.id}")

            if (book.localCoverPath != null) {
                println("Deleting existing cover: ${book.localCoverPath}")
                imageStorage.deleteImage(book.id)
            }

            val localPath = imageStorage.saveCustomImage(coverUri, book.id)
            println("New cover saved at: $localPath")

            if (localPath == null) {
                throw IOException("Failed to save image - localPath is null")
            }

            val updatedBook = book.copy(
                localCoverPath = localPath,
                coverUrl = null  // Clear the URL since we're using a custom cover
            )

            println("Updating book in database with new cover path")
            bookDao.updateBook(updatedBook)
            println("Book updated successfully: $updatedBook")

            return updatedBook
        } catch (e: Exception) {
            println("Error updating book cover: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }


    suspend fun deleteBook(book: Book) {
        book.localCoverPath?.let {
            imageStorage.deleteImage(book.id)
        }
        bookDao.deleteBook(book)
    }


    suspend fun populateTestData() {
        TestData.getTestBooks().forEach { book ->
            addBook(book)
        }
    }

    suspend fun clearAllBooks() {
        bookDao.deleteAllBooks()
    }

    suspend fun getBookById(bookId: String): Book? {
        return bookDao.getBookById(bookId)
    }

    suspend fun doesBookExist(apiId: String, apiSource: String): Boolean {
        val doesExist = bookDao.getBookByApiRef(apiId, apiSource) != null
        println("Checked if book exists: $apiId, $apiSource : $doesExist")
        return doesExist
    }

    suspend fun getBookCount(): Int {
        return bookDao.getBookCount()
    }

    suspend fun getBookCountByStatus(status: ReadingStatus): Int {
        return bookDao.getBookCountByStatus(status)
    }

    /**
     * Gets a book by its API reference (ID and source)
     * @param apiId The ID from the external API (e.g., Google Books ID)
     * @param apiSource The source of the book (e.g., "Google Books")
     * @return The book if found, null otherwise
     */
    suspend fun getBookByApiRef(apiId: String, apiSource: String): Book? {
        return bookDao.getBookByApiRef(apiId, apiSource)
    }


}