package com.practicum.playlistmaker.player.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.intr.AudioPlayerInteractor
import com.practicum.playlistmaker.player.ui.player.AudioplayerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    application: Application,
    private val mediaPlayerInteractor: AudioPlayerInteractor
) : AndroidViewModel(application) {

    private val _playerState = MutableLiveData<AudioplayerState>(AudioplayerState.State_default)
    val playerState: LiveData<AudioplayerState> = _playerState

    private val _currentTime = MutableLiveData<String>("00:00")
    val currentTime: LiveData<String> = _currentTime

    private val _previewUrl = MutableLiveData<String?>()
    val previewUrl: LiveData<String?> = _previewUrl

    fun setPreviewUrl(url: String?) {
        _previewUrl.value = url
        preparePlayerVM(url)
    }

    private fun preparePlayerVM(previewUrl: String?) {
        mediaPlayerInteractor.preparePlayerIntr(
            previewUrl = previewUrl,
            onPrepared = { _playerState.value = AudioplayerState.State_prepared },
            onCompletion = {
                _playerState.value = AudioplayerState.State_completed
                _currentTime.value = "00:00"
            }
        )
    }


    fun startPlayerVM() {
        _playerState.value = AudioplayerState.State_playing
        mediaPlayerInteractor.startPlayerIntr()
        viewModelScope.launch {
            while (playerState.value == AudioplayerState.State_playing) {
                _currentTime.value = mediaPlayerInteractor.getCurrentTimeIntr()
                delay(500)
            }
        }
    }

    fun pausePlayerVM() {
        _playerState.value = AudioplayerState.State_paused
        mediaPlayerInteractor.pausePlayerIntr()
    }

    fun releasePlayerVM() {
        mediaPlayerInteractor.releasePlayerIntr()
    }

    fun playbackControl() {
        when (playerState.value) {
            AudioplayerState.State_default, AudioplayerState.State_completed -> {
                _previewUrl.value?.let { preparePlayerVM(it) }
            }

            AudioplayerState.State_prepared -> startPlayerVM()
            AudioplayerState.State_playing -> pausePlayerVM()
            AudioplayerState.State_paused -> startPlayerVM()

            else -> {} // обработка остальных состояний, если необходимо
        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayerVM()
    }
}