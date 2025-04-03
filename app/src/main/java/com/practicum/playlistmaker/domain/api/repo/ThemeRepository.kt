package com.practicum.playlistmaker.domain.api.repo

interface ThemeRepository {
    fun isDarkThemeEnabledRepo(): Boolean
    fun switchThemeRepo(isDarkTheme: Boolean)
}