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
            val sharedPref = getSharedPreferences("onboarding", MODE_PRIVATE)
            val isOnboardingSeen = sharedPref.getBoolean("onboarding_seen", false)

            if (isOnboardingSeen) {
                // Jika onboarding sudah dilihat, arahkan ke MainActivity (dan LoginFragment jika belum login)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Jika belum, tampilkan OnboardingActivity
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            finish()
        }, 2000) // Splash screen ditampilkan selama 2 detik
    }
}
