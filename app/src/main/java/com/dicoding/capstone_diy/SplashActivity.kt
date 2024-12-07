package com.dicoding.capstone_diy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.capstone_diy.utils.ThemeManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)

            // Menambahkan log untuk memeriksa status login
            Log.d("SplashActivity", "isLoggedIn: $isLoggedIn")

            val onboardingPref = getSharedPreferences("onboarding", MODE_PRIVATE)
            val isOnboardingSeen = onboardingPref.getBoolean("onboarding_seen", false)

            val intent = when {
                // Jika onboarding belum dilihat, arahkan ke OnboardingActivity
                !isOnboardingSeen -> {
                    Intent(this, OnboardingActivity::class.java)
                }
                // Jika sudah login, langsung ke MainActivity yang akan menampilkan HomeFragment
                isLoggedIn -> {
                    Intent(this, MainActivity::class.java).apply {
                        putExtra("navigateToHome", true)
                    }
                }
                // Jika belum login, arahkan ke LoginFragment
                else -> {
                    Intent(this, MainActivity::class.java).apply {
                        putExtra("navigateToLogin", true)
                    }
                }
            }

            startActivity(intent)
            finish()
        }, 2000) // Splash screen ditampilkan selama 2 detik
    }
}


