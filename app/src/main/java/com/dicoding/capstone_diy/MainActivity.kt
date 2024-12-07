package com.dicoding.capstone_diy

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.dicoding.capstone_diy.databinding.ActivityMainBinding
import com.dicoding.capstone_diy.utils.ThemeManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

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

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_statistic, R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)

        // Cek Intent extra untuk menavigasi ke HomeFragment atau LoginFragment
        val navigateToHome = intent.getBooleanExtra("navigateToHome", false)
        val navigateToLogin = intent.getBooleanExtra("navigateToLogin", false)

        if (navigateToHome) {
            // Langsung arahkan ke HomeFragment
            navController.navigate(R.id.navigation_home)
        } else if (navigateToLogin) {
            // Langsung arahkan ke LoginFragment
            navController.navigate(R.id.loginFragment)
        }
    }
}