package de.hs_kl.libris

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import de.hs_kl.libris.util.ThemeManager
import javax.inject.Inject

@HiltAndroidApp
class LibrisApplication : Application() {
    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()
        themeManager.applyTheme()
    }
}