package de.hs_kl.libris

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import de.hs_kl.libris.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_library,
                R.id.navigation_search,
                R.id.navigation_settings
            )
        )

        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.book_detail_fragment -> {
                    val navArgs = navController.currentBackStackEntry?.arguments
                    val bookId = navArgs?.getString("bookId")
                    val previousDestination = navController.previousBackStackEntry?.destination?.id

                    if (bookId != null || previousDestination == R.id.navigation_library) {
                        binding.navView.menu.findItem(R.id.navigation_library).isChecked = true
                    } else if (previousDestination == R.id.navigation_search) {
                        binding.navView.menu.findItem(R.id.navigation_search).isChecked = true
                    }
                }

                else -> {
                    binding.navView.menu.findItem(destination.id)?.isChecked = true
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}