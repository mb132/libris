package de.hs_kl.libris.ui.search

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import de.hs_kl.libris.R
import de.hs_kl.libris.data.model.Book
import de.hs_kl.libris.data.model.ReadingStatus
import de.hs_kl.libris.databinding.ItemSearchResultBinding
import de.hs_kl.libris.util.ImageLoader
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

class SearchResultAdapter(
    private val onBookClick: (SearchResultUiModel) -> Unit,
    private val onAddToLibrary: (SearchResultUiModel) -> Unit
) : ListAdapter<SearchResultUiModel, SearchResultAdapter.ViewHolder>(SearchResultDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: SearchResultUiModel) {
            binding.apply {
                titleText.text = result.title
                authorText.text = result.authors.joinToString(", ")
                sourceText.text = result.source

                ImageLoader.loadBookCover(
                    book = result.toBook(),
                    imageView = coverImage
                )

                addButton.apply {
                    if (result.isInLibrary) {
                        setIconResource(R.drawable.ic_check_circle)
                        isEnabled = false
                    } else {
                        setIconResource(R.drawable.ic_add_black_24dp)
                        isEnabled = true
                        setOnClickListener { onAddToLibrary(result) }
                    }
                    text = null
                    iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                }

                root.setOnClickListener { onBookClick(result) }
            }
        }
    }
}


private class SearchResultDiffCallback : DiffUtil.ItemCallback<SearchResultUiModel>() {
    override fun areItemsTheSame(
        oldItem: SearchResultUiModel,
        newItem: SearchResultUiModel
    ): Boolean {
        return oldItem.id == newItem.id && oldItem.source == newItem.source
    }

    override fun areContentsTheSame(
        oldItem: SearchResultUiModel,
        newItem: SearchResultUiModel
    ): Boolean {
        return oldItem == newItem
    }
}

@Parcelize
data class SearchResultUiModel(
    val id: String,
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
    val source: String,
    val isInLibrary: Boolean,
    val bookId: String? = null
) : Parcelable {
    fun toBook() = Book(
        id = bookId ?: UUID.randomUUID().toString(),
        title = title,
        author = authors.joinToString(", "),
        isbn = isbn,
        pageCount = pageCount,
        coverUrl = coverUrl,
        status = ReadingStatus.NOT_STARTED,
        publisher = publisher,
        publishedDate = publishedDate,
        categories = categories,
        language = language,
        description = description,
        lastModified = Date(),
        apiId = id,
        apiSource = source
    )
}