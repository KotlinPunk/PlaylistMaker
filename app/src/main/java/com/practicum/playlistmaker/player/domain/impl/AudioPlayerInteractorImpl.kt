package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.intr.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.api.repo.AudioPlayerRepository

class AudioPlayerInteractorImpl(private val mediaPlayerRepository: AudioPlayerRepository) :
    AudioPlayerInteractor {
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