package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.intr.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.api.repo.MediaPlayerRepository

class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository) :
    MediaPlayerInteractor {
    override fun preparePlayerIntr(
        previewUrl: String?,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
        onTimeUpdate: (String) -> Unit
    ) {
        mediaPlayerRepository.preparePlayerRepo(previewUrl, onPrepared, onCompletion, onTimeUpdate)
    }

    override fun startPlayerIntr() {
        mediaPlayerRepository.startPlayerRepo()
    }

    override fun pausePlayerIntr() {
        mediaPlayerRepository.pausePlayerRepo()
    }

    override fun releasePlayerIntr() {
        mediaPlayerRepository.releasePlayerRepo()
    }

    override fun isPlayingPlayerIntr(): Boolean {
        return mediaPlayerRepository.isPlayingPlayerRepo()
    }
}