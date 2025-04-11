package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.intr.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.repo.SettingsRepository

// Реализация SettingsInteractor
class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun isDarkThemeEnabledIntr(): Boolean {
        return settingsRepository.isDarkThemeEnabledRepo()
    }

    override fun switchThemeIntr(isDarkTheme: Boolean) {
        return settingsRepository.switchThemeRepo(isDarkTheme)
    }

}