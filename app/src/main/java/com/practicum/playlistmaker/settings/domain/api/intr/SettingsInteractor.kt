package com.practicum.playlistmaker.settings.domain.api.intr

interface SettingsInteractor {
    fun isDarkThemeEnabledIntr(): Boolean
    fun switchThemeIntr(isDarkTheme: Boolean)
}