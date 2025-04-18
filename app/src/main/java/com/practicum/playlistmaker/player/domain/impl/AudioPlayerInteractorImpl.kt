package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.intr.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.api.repo.AudioPlayerRepository

class AudioPlayerInteractorImpl(private val mediaPlayerRepository: AudioPlayerRepository) :
    AudioPlayerInteractor {
    override fun preparePlayerIntr(
        previewUrl: String?,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        mediaPlayerRepository.preparePlayerRepo(previewUrl, onPrepared, onCompletion)
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

    override fun getCurrentTimeIntr(): String {
        return mediaPlayerRepository.getCurrentTimeRepo()
    }
}