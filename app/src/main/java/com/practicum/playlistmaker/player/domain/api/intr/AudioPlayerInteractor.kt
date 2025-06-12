package com.practicum.playlistmaker.player.domain.api.intr

interface AudioPlayerInteractor {
    fun preparePlayerIntr(
        previewUrl: String?,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    )

    fun startPlayerIntr()
    fun pausePlayerIntr()
    fun releasePlayerIntr()
    fun getCurrentTimeIntr(): String
}