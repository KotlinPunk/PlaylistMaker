package com.practicum.playlistmaker.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.intr.SettingsInteractor

class SettingsActivity : AppCompatActivity() {

    private val creator: Creator by lazy { Creator(this) }
    private val settingsInteractor: SettingsInteractor by lazy { creator.settingsInteractor }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrowbackButton = findViewById<ImageButton>(R.id.arrowback)
        val shareButton = findViewById<TextView>(R.id.share)
        val supportButton = findViewById<TextView>(R.id.support)
        val agreementButton = findViewById<TextView>(R.id.agreement)
        val themeSwitcher = findViewById<SwitchCompat>(R.id.themeSwitcher)

        arrowbackButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            startActivity(
                Intent.createChooser(
                    settingsInteractor.getShareIntent(),
                    getString(R.string.share_link)
                )
            )
        }

        supportButton.setOnClickListener {
            startActivity(settingsInteractor.getSupportIntent())
        }

        agreementButton.setOnClickListener {
            startActivity(settingsInteractor.getAgreementIntent())
        }


        themeSwitcher.setChecked(settingsInteractor.isDarkThemeEnabledIntr())

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            settingsInteractor.switchThemeIntr(checked)
        }
    }
}