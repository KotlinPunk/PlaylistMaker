package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    companion object {
        const val APP_PREFERENCES = "practicum_example_app_preferences"
        const val THEME_KEY = "key_for_theme"
    }

    var darkTheme = false
    var sharedPrefs: SharedPreferences? = null

    override fun onCreate() {
        super.onCreate()
        setDarkTheme()
    }

    fun setDarkTheme(): Boolean {
        sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs?.getBoolean(THEME_KEY, darkTheme) == true
        switchTheme(darkTheme)
        return darkTheme
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        sharedPrefs?.edit()
            ?.putBoolean(THEME_KEY, darkTheme)
            ?.apply()
    }
}