package com.practicum.playlistmaker.domain.api.repo

interface MediaPlayerRepository {
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