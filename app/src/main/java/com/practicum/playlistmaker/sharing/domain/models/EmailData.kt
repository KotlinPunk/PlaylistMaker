package com.practicum.playlistmaker.sharing.domain.models

data class EmailData(
    val mailOfRecipient: Array<String>,
    val themeOfMail: String,
    val mailBody: String
)