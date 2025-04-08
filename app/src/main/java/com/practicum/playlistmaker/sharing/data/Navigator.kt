package com.practicum.playlistmaker.sharing.data

import com.practicum.playlistmaker.sharing.domain.models.EmailData

interface Navigator {
    fun getShare(shareLink: String)
    fun getSupport(supportEmailData: EmailData)
    fun getAgreement(agreementLink: String)
}