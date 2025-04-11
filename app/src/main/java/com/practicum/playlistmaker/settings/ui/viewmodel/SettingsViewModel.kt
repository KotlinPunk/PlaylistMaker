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

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val creator: Creator by lazy { Creator(application) }
    private val settingsInteractor: SettingsInteractor by lazy {
        creator.settingsInteractor
    }
    private val sharingInteractor: SharingInteractor by lazy {
        creator.sharingInteractor
    }

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> = _isDarkThemeEnabled

    init {
        loadThemeSettings()
    }

    private fun loadThemeSettings () {
        val isDarkTheme = settingsInteractor.isDarkThemeEnabledIntr()
        _isDarkThemeEnabled.value= isDarkTheme
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

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}