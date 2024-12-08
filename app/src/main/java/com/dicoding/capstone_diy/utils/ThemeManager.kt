package com.dicoding.capstone_diy.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREFS_NAME = "app_preferences"
    private const val KEY_IS_DARK_MODE = "isDarkMode"

    fun applyTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean(KEY_IS_DARK_MODE, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        Log.d("ThemeManager", "applyTheme: isDarkMode = $isDarkMode")
    }

    fun saveThemePreference(context: Context, isDarkMode: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_IS_DARK_MODE, isDarkMode).apply()
        Log.d("ThemeManager", "saveThemePreference: isDarkMode = $isDarkMode")
    }
}
