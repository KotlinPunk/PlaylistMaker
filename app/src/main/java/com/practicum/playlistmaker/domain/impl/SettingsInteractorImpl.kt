package com.practicum.playlistmaker.domain.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.app.App
import com.practicum.playlistmaker.domain.api.intr.SettingsInteractor
import com.practicum.playlistmaker.domain.api.repo.ThemeRepository

// Реализация SettingsInteractor
class SettingsInteractorImpl(
    private val context: Context,
    private val themeRepository: ThemeRepository
) : SettingsInteractor {

    override fun isDarkThemeEnabledIntr(): Boolean {
        return themeRepository.isDarkThemeEnabledRepo()
    }

    override fun switchThemeIntr(isDarkTheme: Boolean) {
        return themeRepository.switchThemeRepo(isDarkTheme)
    }

    override fun getShareIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.course_link))
        }
    }

    override fun getSupportIntent(): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.mail_of_recipient)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString((R.string.theme_of_mail)))
            putExtra(Intent.EXTRA_TEXT, context.getString((R.string.mail_body)))
        }
    }

    override fun getAgreementIntent(): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(context.getString(R.string.link_agreement))
        }
    }
}