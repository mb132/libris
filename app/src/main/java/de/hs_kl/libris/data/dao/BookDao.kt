package de.hs_kl.libris.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.hs_kl.libris.data.model.Book
import de.hs_kl.libris.data.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY lastModified DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: String): Book?

    @Query("SELECT * FROM books WHERE status = :status")
    fun getBooksByStatus(status: ReadingStatus): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM books WHERE apiId = :apiId AND apiSource = :apiSource")
    suspend fun getBookByApiRef(apiId: String, apiSource: String): Book?

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int

    @Query("SELECT COUNT(*) FROM books WHERE status = :status")
    suspend fun getBookCountByStatus(status: ReadingStatus): Int

    @Query("SELECT EXISTS(SELECT 1 FROM books WHERE apiId = :apiId AND apiSource = :apiSource)")
    suspend fun existsByApiId(apiId: String, apiSource: String): Boolean

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()


}