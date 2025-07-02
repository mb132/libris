package de.hs_kl.libris.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.hs_kl.libris.data.dao.BookDao
import de.hs_kl.libris.data.model.Book
import de.hs_kl.libris.data.model.Converters

@Database(
    entities = [Book::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        const val DATABASE_NAME = "libris_db"
    }
}