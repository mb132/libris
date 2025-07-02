package de.hs_kl.libris.ui.components

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.hs_kl.libris.databinding.DialogCategorySelectionBinding

class CategorySelectionDialog : BottomSheetDialogFragment() {
    private var _binding: DialogCategorySelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var availableCategories: List<String>
    private val selectedCategories = mutableSetOf<String>()
    private var onCategoriesSelected: ((List<String>) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext()).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCategorySelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        availableCategories = arguments?.getStringArrayList(ARG_CATEGORIES)?.toList() ?: emptyList()

        setupViews()
        setupSearch()
    }

    private fun setupViews() {
        binding.apply {
            // add category checkboxes
            availableCategories.forEach { category ->
                val checkbox = CheckBox(requireContext()).apply {
                    text = category
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            selectedCategories.add(category)
                        } else {
                            selectedCategories.remove(category)
                        }
                        updateAddButton()
                    }
                }
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                categoryContainer.addView(checkbox, params)
            }

            addButton.setOnClickListener {
                onCategoriesSelected?.invoke(selectedCategories.toList())
                dismiss()
            }
            cancelButton.setOnClickListener { dismiss() }
        }
        updateAddButton()
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener { text ->
            val query = text?.toString()?.lowercase() ?: ""
            binding.categoryContainer.children.forEach { view ->
                if (view is CheckBox) {
                    view.visibility = if (query.isEmpty() ||
                        view.text.toString().lowercase().contains(query)
                    ) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }
        }
    }

    private fun updateAddButton() {
        binding.addButton.isEnabled = selectedCategories.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CATEGORIES = "categories"

        fun newInstance(
            categories: List<String>,
            onCategoriesSelected: (List<String>) -> Unit
        ): CategorySelectionDialog {
            return CategorySelectionDialog().apply {
                arguments = bundleOf(
                    ARG_CATEGORIES to ArrayList(categories)
                )
                this.onCategoriesSelected = onCategoriesSelected
            }
        }
    }
}
