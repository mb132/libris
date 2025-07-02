package de.hs_kl.libris.ui.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import de.hs_kl.libris.R

class AppInfoPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : Preference(context, attrs) {

    init {
        layoutResource = R.layout.preference_app_info
        isSelectable = false
    }
}