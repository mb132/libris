package de.hs_kl.libris.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setTheme(theme: String) {
        val mode = when (theme) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
        prefs.edit().putString(KEY_THEME, theme).apply()
    }

    fun getCurrentTheme(): String {
        return prefs.getString(KEY_THEME, "system") ?: "system"
    }

    fun applyTheme() {
        setTheme(getCurrentTheme())
    }

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME = "current_theme"
    }
}