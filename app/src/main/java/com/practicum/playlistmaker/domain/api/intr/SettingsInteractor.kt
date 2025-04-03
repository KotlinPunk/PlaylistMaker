package com.practicum.playlistmaker.domain.api.intr

import android.content.Intent

interface SettingsInteractor {
    fun isDarkThemeEnabledIntr(): Boolean
    fun switchThemeIntr(isDarkTheme: Boolean)
    fun getShareIntent(): Intent
    fun getSupportIntent(): Intent
    fun getAgreementIntent(): Intent
}