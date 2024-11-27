package com.dicoding.capstone_diy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)

            val onboardingPref = getSharedPreferences("onboarding", MODE_PRIVATE)
            val isOnboardingSeen = onboardingPref.getBoolean("onboarding_seen", false)

            val intent = if (!isOnboardingSeen) {
                Intent(this, OnboardingActivity::class.java)
            } else if (!isLoggedIn) {
                Intent(this, MainActivity::class.java).apply {
                    putExtra("navigateToLogin", true)
                }
            } else {
                Intent(this, MainActivity::class.java)
            }

            startActivity(intent)
            finish()
        }, 2000) // Splash screen ditampilkan selama 2 detik
    }
}

