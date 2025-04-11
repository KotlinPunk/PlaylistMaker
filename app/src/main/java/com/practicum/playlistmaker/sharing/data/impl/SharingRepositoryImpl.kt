package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.Navigator
import com.practicum.playlistmaker.sharing.domain.api.repo.SharingRepository
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class SharingRepositoryImpl(private val context: Context, private val navigator: Navigator) :
    SharingRepository {

    override fun shareAppRepo() {
        navigator.getShare(getShareAppLink())
    }

    override fun openTermsRepo() {
        navigator.getAgreement(getTermsLink())
    }

    override fun openSupportRepo() {
        navigator.getSupport(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.course_link)
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.link_agreement)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            mailOfRecipient = arrayOf(context.getString(R.string.mail_of_recipient)),
            themeOfMail = context.getString((R.string.theme_of_mail)),
            mailBody = context.getString((R.string.mail_body))
        )
    }
}