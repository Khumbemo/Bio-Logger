package com.biologger

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("BioLoggerPrefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("isDarkMode", true)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        installSplashScreen()
        super.onCreate(savedInstanceState)

        // 1. Enable Edge-to-Edge to prevent clashing and fit perfectly
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)

        // 2. Setup 3 main home pages (Home, Tools, Data)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.toolsFragment, R.id.dataFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        // Apply insets to toolbar to avoid status bar clash
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, top, 0, 0)
            insets
        }

        // Apply insets to bottom nav to avoid navigation bar clash
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.setPadding(0, 0, 0, bottom)
            insets
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.toolsFragment, R.id.dataFragment,
                R.id.forestHomeFragment, R.id.greenhouseHomeFragment,
                R.id.gardenHomeFragment, R.id.noteVaultHomeFragment -> {
                    bottomNav.visibility = View.VISIBLE
                    toolbar.visibility = View.VISIBLE
                }
                else -> {
                    bottomNav.visibility = View.GONE
                }
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (navController.currentDestination?.id == R.id.homeFragment) {
                val now = System.currentTimeMillis()
                if (now - backPressedTime < 2000) {
                    finish()
                } else {
                    backPressedTime = now
                    Snackbar.make(findViewById(android.R.id.content), "Press back again to exit", Snackbar.LENGTH_SHORT)
                        .setAnchorView(bottomNav)
                        .show()
                }
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_guidelines -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Bio-Logger Guidelines")
                    .setMessage("Welcome to Bio-Logger.\n\n• Forest Capture: Ecological surveys and timber logic.\n• Agro Climatic Lab: Controlled greenhouse tests.\n• Garden Scape: Horticulture layouts.\n• Note Vault: Storage.\n\nUse the menu to toggle Light/Dark themes dynamically.")
                    .setPositiveButton("Got It", null)
                    .show()
                true
            }
            R.id.action_toggle_theme -> {
                val prefs = getSharedPreferences("BioLoggerPrefs", Context.MODE_PRIVATE)
                val currentlyDark = prefs.getBoolean("isDarkMode", true)
                prefs.edit().putBoolean("isDarkMode", !currentlyDark).apply()
                AppCompatDelegate.setDefaultNightMode(
                    if (!currentlyDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
