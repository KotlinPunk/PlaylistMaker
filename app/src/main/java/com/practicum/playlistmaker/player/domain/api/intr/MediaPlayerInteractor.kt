package com.practicum.playlistmaker.player.domain.api.intr

interface MediaPlayerInteractor {
    fun preparePlayerIntr(
        previewUrl: String?,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
        onTimeUpdate: (String) -> Unit
    )

    fun startPlayerIntr()
    fun pausePlayerIntr()
    fun releasePlayerIntr()
    fun isPlayingPlayerIntr(): Boolean
}