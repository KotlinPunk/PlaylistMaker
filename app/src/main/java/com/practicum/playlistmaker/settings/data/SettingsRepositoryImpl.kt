package com.practicum.playlistmaker.settings.data

import android.content.Context
import com.practicum.playlistmaker.app.App
import com.practicum.playlistmaker.settings.domain.api.repo.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private val app = context.applicationContext as App
    override fun isDarkThemeEnabledRepo(): Boolean {
        return app.setDarkTheme()
    }

    override fun switchThemeRepo(isDarkTheme: Boolean) {
        app.switchTheme(isDarkTheme)
    }
}