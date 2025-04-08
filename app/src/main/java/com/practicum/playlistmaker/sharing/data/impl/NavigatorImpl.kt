package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.Navigator
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class NavigatorImpl(private val context: Context) : Navigator {

    override fun getShare(shareLink: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareLink)
        }
        val intentChoice = Intent.createChooser(intent, context.getString(R.string.share_link))
        startActivityIntent(intentChoice)
    }

    override fun getSupport(supportEmailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, supportEmailData.mailOfRecipient)
            putExtra(Intent.EXTRA_SUBJECT, supportEmailData.themeOfMail)
            putExtra(Intent.EXTRA_TEXT, supportEmailData.mailBody)
        }
        startActivityIntent(intent)
    }

    override fun getAgreement(agreementLink: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(agreementLink)
        }
        startActivityIntent(intent)
    }

    private fun startActivityIntent(intent: Intent) {
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                context.applicationContext,
                context.getString(R.string.toast_error_intent),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
