package com.dicoding.capstone_diy

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dicoding.capstone_diy.databinding.ActivityOnboardingBinding
import com.dicoding.capstone_diy.ui.onboarding.OnboardingAdapter
import com.dicoding.capstone_diy.ui.onboarding.OnboardingItem

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("onboarding", MODE_PRIVATE)

        val onboardingItems = listOf(
            OnboardingItem(
                "Discover Inner You",
                "Find out more about yourself with DIY.",
                R.drawable.onboarding_1
            ),
            OnboardingItem(
                "Write Anytime",
                "Write your feelings anytime, anywhere.",
                R.drawable.onboarding_2
            ),
            OnboardingItem(
                "Your Data is Safe",
                "Your data is secure with us! let's go create something and feel your self",
                R.drawable.onboarding_3
            )
        )

        val adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter

        binding.buttonNext.setImageResource(R.drawable.ic_arrow_right)
        Log.d("OnboardingActivity", "ButtonNext scaleX: ${binding.buttonNext.scaleX}")
        setIndicator(0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setIndicator(position)
            }
        })

        binding.buttonNext.setOnClickListener {
            val viewPager = binding.viewPager
            if (viewPager.currentItem < onboardingItems.size - 1) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                saveOnboardingSeen()
                navigateToMain()
            }
        }
    }

    private fun setIndicator(position: Int) {
        val smallSize = 20
        val largeSize = 25

        val indicators = listOf(
            binding.indicator1,
            binding.indicator2,
            binding.indicator3
        )

        for (i in indicators.indices) {
            val layoutParams = indicators[i].layoutParams
            layoutParams.width = smallSize
            layoutParams.height = smallSize
            indicators[i].layoutParams = layoutParams
            indicators[i].setImageResource(R.drawable.circle_inactive)
        }

        val activeIndicator = indicators[position]
        val activeLayoutParams = activeIndicator.layoutParams
        activeLayoutParams.width = largeSize
        activeLayoutParams.height = largeSize
        activeIndicator.layoutParams = activeLayoutParams
        activeIndicator.setImageResource(R.drawable.circle_active)
    }


    private fun saveOnboardingSeen() {
        sharedPreferences.edit()
            .putBoolean("onboarding_seen", true)
            .apply()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
