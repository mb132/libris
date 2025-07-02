package de.hs_kl.libris.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStorage @Inject constructor(
    private val context: Context
) {
    private val imageDir: File by lazy {
        File(context.filesDir, "book_covers").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    suspend fun saveImage(imageUrl: String?, bookId: String): String? =
        withContext(Dispatchers.IO) {
            try {
                // if no URL is provided, return null without attempting to save
                if (imageUrl == null) return@withContext null

                // generate a unique filename using the bookid
                val fileName = "cover_$bookId.jpg"
                val imageFile = File(imageDir, fileName)

                // download image
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()

                FileOutputStream(imageFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }

                return@withContext imageFile.absolutePath
            } catch (e: Exception) {
                return@withContext null
            }
        }

    suspend fun saveCustomImage(uri: Uri, bookId: String): String? = withContext(Dispatchers.IO) {
        try {
            val fileName = "cover_${bookId}_${System.currentTimeMillis()}.jpg"
            val imageFile = File(imageDir, fileName)

            deleteImage(bookId)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output)
                }
            } ?: throw IOException("Failed to open input stream")

            if (!imageFile.exists() || imageFile.length() == 0L) {
                throw IOException("File was not created successfully")
            }

            withContext(Dispatchers.Main) {
                Glide.get(context).clearMemory()
            }
            withContext(Dispatchers.IO) {
                Glide.get(context).clearDiskCache()
            }

            return@withContext imageFile.absolutePath
        } catch (e: Exception) {
            println("Failed to save image: ${e.message}")
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun getImageFile(bookId: String): File {
        return File(imageDir, "cover_$bookId.jpg")
    }

    fun deleteImage(bookId: String) {
        val deletedFiles = imageDir.listFiles { file ->
            file.name.startsWith("cover_$bookId")
        }?.map { it.delete() }

        println("Deleted ${deletedFiles?.count { it }} files for bookId: $bookId")
    }

    fun hasLocalImage(bookId: String): Boolean {
        return getImageFile(bookId).exists()
    }

    fun getCacheKey(bookId: String): String {
        return imageDir.listFiles { file ->
            file.name.startsWith("cover_$bookId")
        }?.firstOrNull()?.name ?: ""
    }
}


