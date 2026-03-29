package com.biologger

<<<<<<< HEAD
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
=======
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
>>>>>>> master
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.appbar.MaterialToolbar
<<<<<<< HEAD
import com.google.android.material.snackbar.Snackbar
=======
>>>>>>> master

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
<<<<<<< HEAD
    private var backPressedTime = 0L
=======
>>>>>>> master

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

<<<<<<< HEAD
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mainDashboardFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, top, 0, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.setPadding(0, 0, 0, bottom)
            insets
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainDashboardFragment -> {
                    bottomNav.visibility = View.VISIBLE
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    toolbar.title = ""
                    toolbar.navigationIcon = null
                }
                R.id.forestHomeFragment -> {
                    bottomNav.visibility = View.VISIBLE
                    toolbar.setBackgroundResource(R.color.forest_green)
                    toolbar.title = "Forest Capture"
                }
                R.id.greenhouseHomeFragment -> {
                    bottomNav.visibility = View.VISIBLE
                    toolbar.setBackgroundResource(R.color.greenhouse_teal)
                    toolbar.title = "AgroClimatic Lab"
                }
                R.id.gardenHomeFragment -> {
                    bottomNav.visibility = View.VISIBLE
                    toolbar.setBackgroundResource(R.color.garden_amber)
                    toolbar.title = "Garden Scape"
                }
                R.id.noteVaultHomeFragment -> {
                    bottomNav.visibility = View.VISIBLE
                    toolbar.setBackgroundResource(R.color.vault_purple)
                    toolbar.title = "Note Vault"
                }
                else -> {
                    bottomNav.visibility = View.GONE
                    toolbar.setBackgroundResource(R.color.white)
                    toolbar.title = destination.label ?: ""
                }
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (navController.currentDestination?.id == R.id.mainDashboardFragment) {
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
=======
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainDashboardFragment,
                R.id.forestHomeFragment,
                R.id.greenhouseHomeFragment,
                R.id.gardenHomeFragment,
                R.id.noteVaultHomeFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainDashboardFragment,
                R.id.forestHomeFragment,
                R.id.greenhouseHomeFragment,
                R.id.gardenHomeFragment,
                R.id.noteVaultHomeFragment -> {
                    bottomNav.visibility = View.VISIBLE
                }
                else -> {
                    bottomNav.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
>>>>>>> master
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
