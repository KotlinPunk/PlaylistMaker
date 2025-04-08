package com.practicum.playlistmaker.settings.domain.api.repo

interface SettingsRepository {
    fun isDarkThemeEnabledRepo(): Boolean
    fun switchThemeRepo(isDarkTheme: Boolean)
}