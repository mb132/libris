package de.hs_kl.libris.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import de.hs_kl.libris.R
import de.hs_kl.libris.data.model.Book
import java.io.File
import java.util.concurrent.TimeUnit

object ImageLoader {
    private const val THUMBNAIL_SIZE = 250  // Size for list view
    private const val DETAIL_SIZE = 1000    // Size for detail view

    fun loadBookCover(
        book: Book,
        imageView: ImageView,
        enableTransition: Boolean = false,
        isDetailView: Boolean = false
    ) {
        val imageSource = getImageSource(book)

        // Early return if no image source available
        if (imageSource == null) {
            try {
                Glide.with(imageView)
                    .load(R.drawable.placeholder_no_cover)
                    .into(imageView)
            } catch (e: Exception) {
                imageView.setImageResource(R.drawable.placeholder_no_cover)
            }
            return
        }

        try {
            val request = Glide.with(imageView)
                .load(imageSource)
                .diskCacheStrategy(getDiskCacheStrategy(book))
                .skipMemoryCache(false)
                .timeout(10000)
                .placeholder(R.drawable.placeholder_no_cover)
                .error(R.drawable.placeholder_no_cover)
                .override(if (isDetailView) DETAIL_SIZE else THUMBNAIL_SIZE)

            if (enableTransition) {
                request.transition(DrawableTransitionOptions.withCrossFade())
            }

            request.into(imageView)
        } catch (e: Exception) {
            // fallback to simple ImageView
            try {
                imageView.setImageResource(R.drawable.placeholder_no_cover)
            } catch (e: Exception) {
                // Ignore setting placeholder fails too :c
            }
        }
    }

    fun preloadHighQuality(book: Book, context: Context) {
        val imageSource = getImageSource(book) ?: return

        try {
            Glide.with(context.applicationContext)
                .load(imageSource)
                .override(DETAIL_SIZE)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .priority(Priority.LOW)
                .preload()
        } catch (e: Exception) {
        }
    }

    fun cancelPreload(book: Book, context: Context) {
        try {
            // Use application context to avoid activity-related issues
            val glide = Glide.with(context.applicationContext)
            glide.clear(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                ) {
                    // No implementation needed
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // No implementation needed
                }
            })
        } catch (e: Exception) {
            // Ignore cancellation failures
        }
    }

    fun isHighQualityReady(book: Book, context: Context): Boolean {
        val imageSource = getImageSource(book) ?: return false

        return try {
            Glide.with(context.applicationContext)
                .load(imageSource)
                .override(DETAIL_SIZE)
                .onlyRetrieveFromCache(true)
                .submit()
                .get(1, TimeUnit.MILLISECONDS) != null
        } catch (e: Exception) {
            false
        }
    }

    private fun getImageSource(book: Book): Any? {
        return book.localCoverPath?.let { path ->
            val file = File(path)
            if (file.exists() && file.length() > 0) file else null
        } ?: book.coverUrl
    }

    private fun getDiskCacheStrategy(book: Book): DiskCacheStrategy {
        return when {
            book.localCoverPath != null -> DiskCacheStrategy.NONE
            else -> DiskCacheStrategy.AUTOMATIC
        }
    }
}
