package com.practicum.playlistmaker.player.domain.api.repo

interface AudioPlayerRepository {
    fun preparePlayerRepo(
        previewUrl: String?,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
        onTimeUpdate: (String) -> Unit
    )

    fun startPlayerRepo()
    fun pausePlayerRepo()
    fun releasePlayerRepo()
    fun isPlayingPlayerRepo(): Boolean
}