package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrowbackButton = findViewById<ImageButton>(R.id.arrowback)
        val shareButton = findViewById<TextView>(R.id.share)
        val supportButton = findViewById<TextView>(R.id.support)
        val agreementButton = findViewById<TextView>(R.id.agreement)
        val themeSwitcher = findViewById<Switch>(R.id.themeSwitcher)

        arrowbackButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_link)))
        }

        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_of_recipient)))
                putExtra(Intent.EXTRA_SUBJECT, getString((R.string.theme_of_mail)))
                putExtra(Intent.EXTRA_TEXT, getString((R.string.mail_body)))
            }
            startActivity(supportIntent)
        }

        agreementButton.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.data = Uri.parse(getString(R.string.link_agreement))
            startActivity(agreementIntent)
        }

        themeSwitcher.setChecked((applicationContext as App).setDarkTheme())

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}