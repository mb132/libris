package de.hs_kl.libris.ui.components

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton


class PageInputDialog : DialogFragment() {
    private var currentPage: Int = 0
    private var maxPage: Int = 0
    private var onPageSet: ((Int) -> Unit)? = null
    private lateinit var input: EditText
    private lateinit var progressText: TextView
    private lateinit var progressPercentText: TextView
    private var isUpdatingText = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 24, 32, 24)
                setBackgroundColor(Color.parseColor("#1F1F1F"))

                progressText = TextView(context).apply {
                    text = "$currentPage → / $maxPage"
                    gravity = Gravity.CENTER
                    textSize = 20f
                    setTextColor(Color.WHITE)
                }
                addView(progressText)

                val incrementsLayout = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                    setPadding(0, 24, 0, 24)

                    // add increment buttons: +10, +25, +50
                    listOf(10, 25, 50).forEach { increment ->
                        addView(createIncrementButton(increment))
                    }
                }
                addView(incrementsLayout)

                input = EditText(context).apply {
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER
                    setText(currentPage.toString())
                    selectAll()
                    gravity = Gravity.CENTER
                    textSize = 20f
                    setTextColor(Color.WHITE)
                    background = null

                    //  keyboard done/enter action
                    imeOptions = EditorInfo.IME_ACTION_DONE
                    setOnEditorActionListener { _, actionId, _ ->
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            saveAndDismiss()
                            true
                        } else {
                            false
                        }
                    }

                    addTextChangedListener { text ->
                        if (!isUpdatingText) {
                            val newPage = text.toString().toIntOrNull() ?: currentPage
                            if (newPage <= maxPage) {
                                onPageSet?.invoke(newPage)
                                updateProgress(newPage, false)
                            }
                        }
                    }

                    // handle focus loss
                    setOnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            saveAndDismiss()
                        }
                    }
                }
                addView(input)

                // progress percentage
                progressPercentText = TextView(context).apply {
                    text = "${calculateProgress()}% Complete"
                    gravity = Gravity.CENTER
                    textSize = 14f
                    setTextColor(Color.GRAY)
                    setPadding(0, 16, 0, 0)
                }
                addView(progressPercentText)
            }

            setContentView(layout)

            window?.apply {
                setBackgroundDrawable(null)
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setGravity(Gravity.BOTTOM)
                attributes?.windowAnimations = android.R.style.Animation_InputMethod
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }

            setCanceledOnTouchOutside(true)
        }
    }

    private fun createIncrementButton(increment: Int): MaterialButton {
        return MaterialButton(requireContext()).apply {
            text = "+$increment"
            setOnClickListener {
                val newPage = minOf(currentPage + increment, maxPage)
                updateProgress(newPage, true)
                onPageSet?.invoke(newPage)
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 8
                marginEnd = 8
            }
        }
    }

    private fun updateProgress(newPage: Int, updateInput: Boolean) {
        currentPage = newPage
        progressText.text = "$currentPage → / $maxPage"
        progressPercentText.text = "${calculateProgress()}% Complete"

        if (updateInput) {
            isUpdatingText = true
            input.setText(newPage.toString())
            isUpdatingText = false
        }
    }

    private fun calculateProgress(): Int {
        return ((currentPage.toFloat() / maxPage.toFloat()) * 100).toInt()
    }

    private fun saveAndDismiss() {
        val newPage = input.text.toString().toIntOrNull() ?: currentPage
        if (newPage <= maxPage) {
            onPageSet?.invoke(newPage)
        }
        dismiss()
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0)
    }

    override fun onDismiss(dialog: DialogInterface) {
        hideKeyboard()
        super.onDismiss(dialog)
    }

    override fun onResume() {
        super.onResume()
        input.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        onPageSet = null
    }

    companion object {
        fun newInstance(
            currentPage: Int,
            maxPage: Int,
            onPageSet: (Int) -> Unit
        ): PageInputDialog {
            return PageInputDialog().apply {
                this.currentPage = currentPage
                this.maxPage = maxPage
                this.onPageSet = onPageSet
            }
        }
    }
}