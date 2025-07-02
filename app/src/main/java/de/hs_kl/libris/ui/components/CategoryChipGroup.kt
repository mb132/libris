package de.hs_kl.libris.ui.components

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.chip.Chip
import de.hs_kl.libris.R
import de.hs_kl.libris.databinding.ViewCategoryChipsBinding

class CategoryChipGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewCategoryChipsBinding.inflate(LayoutInflater.from(context), this)
    private var selectedCategories = mutableSetOf<String>()
    private var onCategoryChangeListener: ((Set<String>) -> Unit)? = null
    private var isEditMode = false

    init {
        setupAddChip()
        binding.addChip.chipIconTint = context.getColorStateList(R.color.on_surface_high)
    }

    private fun setupAddChip() {
        binding.addChip.setOnClickListener {
            showCategoryDialog()
        }
    }

    private fun showCategoryDialog() {
        val categories = context.resources.getStringArray(R.array.book_genres).toList()
        val unselectedCategories = categories.filter { it !in selectedCategories }

        // find  FragmentActivity
        var currentContext = context
        while (currentContext is ContextWrapper && currentContext !is FragmentActivity) {
            currentContext = currentContext.baseContext
        }

        if (currentContext is FragmentActivity) {
            CategorySelectionDialog.newInstance(
                unselectedCategories,
                onCategoriesSelected = { newCategories ->
                    addCategories(newCategories)
                }
            ).show(currentContext.supportFragmentManager, "category_dialog")
        } else {
            throw IllegalStateException("CategoryChipGroup must be used within a FragmentActivity")
        }
    }

    fun setCategories(categories: List<String>) {
        selectedCategories.clear()
        selectedCategories.addAll(categories)
        updateChips()
    }

    fun getSelectedCategories(): List<String> = selectedCategories.toList()

    fun setOnCategoryChangeListener(listener: (Set<String>) -> Unit) {
        onCategoryChangeListener = listener
    }

    fun setEditMode(enabled: Boolean) {
        isEditMode = enabled
        binding.addChip.visibility = if (enabled) VISIBLE else GONE
        updateChips()
    }

    private fun addCategories(categories: List<String>) {
        selectedCategories.addAll(categories)
        updateChips()
        onCategoryChangeListener?.invoke(selectedCategories)
    }

    private fun removeCategory(category: String) {
        selectedCategories.remove(category)
        updateChips()
        onCategoryChangeListener?.invoke(selectedCategories)
    }

    private fun updateChips() {
        binding.chipGroup.removeAllViews()

        selectedCategories.forEach { category ->
            addChip(category)
        }

        if (isEditMode) {
            binding.chipGroup.addView(binding.addChip)
        }
    }

    private fun addChip(category: String) {
        val chip = Chip(context).apply {
            text = category
            isCloseIconVisible = isEditMode
            isCheckable = false
            setOnCloseIconClickListener {
                removeCategory(category)
            }
        }
        binding.chipGroup.addView(chip)
    }
}

