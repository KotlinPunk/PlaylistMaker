package com.practicum.playlistmaker.data.impl

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.domain.api.repo.MediaPlayerRepository
import java.util.Locale

class MediaPlayerRepositoryImpl() : MediaPlayerRepository {

    private val mediaPlayer = MediaPlayer()
    private val dateFormat by lazy {
        android.icu.text.SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var playerState = STATE_DEFAULT
    private var updateTimeRunnable: Runnable? = null

    override fun preparePlayerRepo(
        previewUrl: String?,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
        onTimeUpdate: (String) -> Unit
    ) {
        try {
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
                onPrepared()
                startTimer()
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                onCompletion()
                stopTimer()
            }
            updateTimeRunnable = object : Runnable {
                override fun run() {
                    if (playerState == STATE_PLAYING) {
                        onTimeUpdate(dateFormat.format(mediaPlayer.currentPosition))
                        mainThreadHandler.postDelayed(this, UPDATE_TIMER)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun startPlayerRepo() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        startTimer()
    }

    override fun pausePlayerRepo() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        stopTimer()
    }

    override fun releasePlayerRepo() {
        stopTimer()
        mediaPlayer.release()
    }

    override fun isPlayingPlayerRepo(): Boolean {
        return playerState == STATE_PLAYING
    }

    private fun startTimer() {
        mainThreadHandler.post(updateTimeRunnable!!)
    }

    private fun stopTimer() {
        updateTimeRunnable?.let(mainThreadHandler::removeCallbacks)
    }

    companion object {
        private const val UPDATE_TIMER = 500L

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}