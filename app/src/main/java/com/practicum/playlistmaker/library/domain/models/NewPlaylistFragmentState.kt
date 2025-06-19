package com.practicum.playlistmaker.library.domain.models

data class NewPlaylistFragmentState(
    val namePL: String = "",
    val descriptionPL: String = "",
    val coverPathPL: String = "",
    val isNameValid: Boolean = false, // флаг для валидации имени
    val isSaveButtinEnabled: Boolean = false, //флаг для кнопки "Создать"
    val saveResult: Long? = null, // id нового плейлиста после сохранения
    val saveError: Throwable? = null // информация об ошибке
)
