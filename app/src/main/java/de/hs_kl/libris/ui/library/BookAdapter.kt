package de.hs_kl.libris.ui.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.hs_kl.libris.data.model.Book
import de.hs_kl.libris.databinding.ItemBookBinding
import de.hs_kl.libris.util.ImageLoader

class BookAdapter(
    private val onBookClick: (String) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
        holder.itemView.setOnClickListener {
            onBookClick(book.id)
        }
    }

    inner class BookViewHolder(
        private val binding: ItemBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.titleText.text = book.title
            binding.authorText.text = book.author

            // load thumbnail
            ImageLoader.loadBookCover(book, binding.coverImage)

            // clean up any existing listeners
            binding.root.tag?.let { tag ->
                if (tag is View.OnAttachStateChangeListener) {
                    binding.root.removeOnAttachStateChangeListener(tag)
                }
            }

            // only add preloading if the view is currently attached
            if (binding.root.isAttachedToWindow) {
                val stateChangeListener = object : View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(v: View) {
                        try {
                            ImageLoader.preloadHighQuality(book, v.context.applicationContext)
                        } catch (e: Exception) {
                            // ignore exceptions
                        }
                    }

                    override fun onViewDetachedFromWindow(v: View) {
                        try {
                            ImageLoader.cancelPreload(book, v.context.applicationContext)
                        } catch (e: Exception) {
                        }
                    }
                }

                binding.root.tag = stateChangeListener
                binding.root.addOnAttachStateChangeListener(stateChangeListener)

                try {
                    ImageLoader.preloadHighQuality(book, binding.root.context.applicationContext)
                } catch (e: Exception) {
                }
            }

            binding.root.setOnClickListener {
                onBookClick(book.id)
            }
        }

        fun unbind() {
            binding.root.tag?.let { tag ->
                if (tag is View.OnAttachStateChangeListener) {
                    binding.root.removeOnAttachStateChangeListener(tag)
                }
            }
            binding.root.tag = null
        }
    }

    override fun onViewRecycled(holder: BookViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

}

private class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem == newItem
    }
}

