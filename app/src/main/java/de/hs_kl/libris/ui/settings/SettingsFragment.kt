package de.hs_kl.libris.ui.settings

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import de.hs_kl.libris.R
import de.hs_kl.libris.util.ThemeManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var viewModel: SettingsViewModel

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        setupLibraryPreferences()
        setupThemePreference()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.statisticsText.collect { stats ->
                    findPreference<Preference>("library_stats")?.summary = stats
                }
            }
        }

        viewModel.observeStatistics(requireContext())
    }

    private fun setupLibraryPreferences() {
        // Fill Library
        findPreference<Preference>("fill_library")?.setOnPreferenceClickListener {
            showFillLibraryDialog()
            true
        }

        // Clear Library
        findPreference<Preference>("clear_library")?.setOnPreferenceClickListener {
            showClearLibraryDialog()
            true
        }
    }

    private fun setupThemePreference() {
        findPreference<ListPreference>(getString(R.string.theme_title))?.apply {
            value = themeManager.getCurrentTheme()

            setOnPreferenceChangeListener { _, newValue ->
                val themeValue = newValue as String
                themeManager.setTheme(themeValue)
                true
            }

            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }
    }

    private fun showClearLibraryDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_clear_library_title)
            .setMessage(R.string.dialog_clear_library_message)
            .setPositiveButton(R.string.dialog_clear_library_positive) { _, _ ->
                viewModel.clearLibrary()
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }

    private fun showFillLibraryDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_fill_library_title)
            .setMessage(R.string.dialog_fill_library_message)
            .setPositiveButton(R.string.dialog_fill_library_positive) { _, _ ->
                viewModel.populateTestData()
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }
}