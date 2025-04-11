package com.practicum.playlistmaker.sharing.data.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.Navigator
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class NavigatorImpl(private val context: Context) : Navigator {

    private val tag = "NavigatorImpl"

    override fun getShare(shareLink: String) {
        Log.d(tag, "Попытка поделиться ссылкой: $shareLink")
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareLink)
        }
        val intentChoice = Intent.createChooser(intent, context.getString(R.string.share_link))
        startActivityIntent(intentChoice)
    }

    override fun getSupport(supportEmailData: EmailData) {
        Log.d(tag, "Попытка обратиться в поддержку $supportEmailData")
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, supportEmailData.mailOfRecipient)
            putExtra(Intent.EXTRA_SUBJECT, supportEmailData.themeOfMail)
            putExtra(Intent.EXTRA_TEXT, supportEmailData.mailBody)
        }
        startActivityIntent(intent)
    }

    override fun getAgreement(agreementLink: String) {
        Log.d(tag, "Попытка открыть ссылку на соглашение: $agreementLink")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(agreementLink)
        }
        startActivityIntent(intent)
    }

    private fun startActivityIntent(intent: Intent) {
        try {
            Log.d(tag, "Старт интента: ${intent.action} - ${intent.data}")
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(tag, "Ошибка старта интента: ${e.message}", e)
            Toast.makeText(
                context.applicationContext,
                context.getString(R.string.toast_error_intent),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
