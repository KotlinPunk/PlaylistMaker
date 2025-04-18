package com.practicum.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.repo.AudioPlayerRepository
import com.practicum.playlistmaker.player.ui.player.AudioplayerState
import java.util.Locale

class AudioPlayerRepositoryImpl() : AudioPlayerRepository {

    private var mediaPlayer: MediaPlayer? = null
    private var playerState: AudioplayerState = AudioplayerState.State_default

    private val dateFormat by lazy {
        android.icu.text.SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }

    override fun preparePlayerRepo(
        previewUrl: String?,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,

        ) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onCompletion() }
        }
    }

    override fun startPlayerRepo() {
        mediaPlayer?.start()
        playerState = AudioplayerState.State_playing
    }

    override fun pausePlayerRepo() {
        mediaPlayer?.pause()
        playerState = AudioplayerState.State_paused

    }

    override fun releasePlayerRepo() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun getCurrentTimeRepo(): String {
        return dateFormat.format(mediaPlayer?.currentPosition)
    }
}