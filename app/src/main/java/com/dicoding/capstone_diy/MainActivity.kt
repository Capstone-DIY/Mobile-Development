package com.dicoding.capstone_diy

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.dicoding.capstone_diy.databinding.ActivityMainBinding
import com.dicoding.capstone_diy.utils.ThemeManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this) // Terapkan tema sebelum onCreate

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)


        // Menyembunyikan BottomNavigationView di LoginFragment dan SignUpFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.signUpFragment -> {
                    navView.visibility = View.GONE
                }
                else -> {
                    navView.visibility = View.VISIBLE
                }
            }
        }

        supportActionBar?.hide()

//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_statistic, R.id.navigation_profile
//            )
//        )
//        navView.setupWithNavController(navController)

// Listener untuk navigasi manual dengan animasi
        navView.setOnItemSelectedListener { menuItem ->
            val currentDestination = navController.currentDestination?.id
            val targetDestination = when (menuItem.itemId) {
                R.id.navigation_home -> R.id.navigation_home
                R.id.navigation_statistic -> R.id.navigation_statistic
                R.id.navigation_profile -> R.id.navigation_profile
                else -> null
            }

            if (targetDestination != null && targetDestination != currentDestination) {
                val navOptions = when {
                    // Home ke Statistic atau Statistic ke Profile: geser kiri
                    (currentDestination == R.id.navigation_home && targetDestination == R.id.navigation_statistic) ||
                            (currentDestination == R.id.navigation_statistic && targetDestination == R.id.navigation_profile) ||
                                (currentDestination == R.id.navigation_home && targetDestination == R.id.navigation_profile)-> {
                        NavOptions.Builder()
                            .setEnterAnim(R.anim.slide_in_right)
                            .setExitAnim(R.anim.slide_out_left)
                            .setPopEnterAnim(R.anim.slide_in_left)
                            .setPopExitAnim(R.anim.slide_out_right)
                            .build()
                    }
                    // Statistic ke Home atau Profile ke Statistic: geser kanan
                    (currentDestination == R.id.navigation_statistic && targetDestination == R.id.navigation_home) ||
                            (currentDestination == R.id.navigation_profile && targetDestination == R.id.navigation_statistic) ||
                                (currentDestination == R.id.navigation_profile && targetDestination == R.id.navigation_home)-> {
                        NavOptions.Builder()
                            .setEnterAnim(R.anim.slide_in_left)
                            .setExitAnim(R.anim.slide_out_right)
                            .setPopEnterAnim(R.anim.slide_in_right)
                            .setPopExitAnim(R.anim.slide_out_left)
                            .build()
                    }
                    else -> null
                }

                if (navOptions != null) {
                    navController.navigate(targetDestination, null, navOptions)
                }
                true
            } else {
                false
            }
        }



        // Pulihkan fragment terakhir jika Activity dimuat ulang
//        val sharedPref = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
//        val lastFragment = sharedPref.getInt("lastFragment", R.id.navigation_home) // Default ke Home
        val sharedPreff = getSharedPreferences("user", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreff.getBoolean("is_logged_in", false)
        Log.d("MainActivity", "isLoggedIn: $isLoggedIn")


        // Navigasikan ke fragment terakhir
        if (savedInstanceState == null) {
            if (isLoggedIn) {
//                navController.navigate(lastFragment)
//                navView.menu.findItem(lastFragment).isChecked = true
                navController.navigate(R.id.navigation_home)
            } else {
                navController.navigate(R.id.loginFragment)
            }
        }


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Terapkan tema ulang jika terjadi perubahan konfigurasi
        ThemeManager.applyTheme(this)
    }
}