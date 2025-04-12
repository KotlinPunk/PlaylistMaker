package com.practicum.playlistmaker.settings.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.domain.api.intr.SettingsInteractor

import com.practicum.playlistmaker.sharing.domain.api.intr.SharingInteractor
import com.practicum.playlistmaker.utils.Creator

class SettingsViewModel(
    application: Application,
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : AndroidViewModel(application) {

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> = _isDarkThemeEnabled

    init {
        loadThemeSettings()
    }

    private fun loadThemeSettings() {
        val isDarkTheme = settingsInteractor.isDarkThemeEnabledIntr()
        _isDarkThemeEnabled.value = isDarkTheme
    }

    fun switchTheme(isDark: Boolean) {
        settingsInteractor.switchThemeIntr(isDark)
        loadThemeSettings()
    }

    fun shareApp() {
        sharingInteractor.shareAppIntr()
    }

    fun openSupport() {
        sharingInteractor.openSupportIntr()
    }

    fun openTerms() {
        sharingInteractor.openTermsIntr()
    }
}