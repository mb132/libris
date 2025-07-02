package de.hs_kl.libris.util

import android.content.Context
import de.hs_kl.libris.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    private val context: Context
) {
    // Map of ISO codes to resource array indices
    private val isoToIndexMap = buildLanguageMap()

    private fun buildLanguageMap(): Map<String, Int> {
        val languageCodes = context.resources.getStringArray(R.array.language_codes)
        return languageCodes.withIndex().associate { it.value to it.index }
    }

    fun getISOCode(displayName: String): String? {
        val languages = context.resources.getStringArray(R.array.languages)
        val codes = context.resources.getStringArray(R.array.language_codes)
        val index = languages.indexOf(displayName)
        return if (index != -1) codes[index] else null
    }

    fun getDisplayName(isoCode: String): String? {
        val index = isoToIndexMap[isoCode] ?: return null
        return context.resources.getStringArray(R.array.languages)[index]
    }

    fun getAllLanguages(): List<String> {
        return context.resources.getStringArray(R.array.languages).toList()
    }
}