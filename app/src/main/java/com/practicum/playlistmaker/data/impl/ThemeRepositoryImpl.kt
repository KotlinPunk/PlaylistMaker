package com.practicum.playlistmaker.data.impl

import android.content.Context
import com.practicum.playlistmaker.app.App
import com.practicum.playlistmaker.domain.api.repo.ThemeRepository

class ThemeRepositoryImpl(private val context: Context) : ThemeRepository {
    private val app = context.applicationContext as App
    override fun isDarkThemeEnabledRepo(): Boolean {
        return app.setDarkTheme()
    }

    override fun switchThemeRepo(isDarkTheme: Boolean) {
        return app.switchTheme(isDarkTheme)
    }
}