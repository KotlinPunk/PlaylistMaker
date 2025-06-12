package com.practicum.playlistmaker.player.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.intr.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.models.AudioplayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    application: Application,
    private val mediaPlayerInteractor: AudioPlayerInteractor
) : AndroidViewModel(application) {

    private var timerJob: Job? = null
    private var currentPreviewUrl: String? = null

    private val _playerState = MutableLiveData<AudioplayerState>(AudioplayerState.State_default)
    val playerState: LiveData<AudioplayerState> = _playerState

    private val _currentTime = MutableLiveData<String>("00:00")
    val currentTime: LiveData<String> = _currentTime

    private val _previewUrl = MutableLiveData<String?>()
    val previewUrl: LiveData<String?> = _previewUrl

    fun setPreviewUrl(url: String?) {
        _previewUrl.value = url
        currentPreviewUrl = url //сохранили адрес
        preparePlayerVM(url)
    }

    private fun preparePlayerVM(previewUrl: String?) {
        if (previewUrl == null) return // проверили на null
        mediaPlayerInteractor.preparePlayerIntr(
            previewUrl = previewUrl,
            onPrepared = { _playerState.value = AudioplayerState.State_prepared },
            onCompletion = {
                _playerState.value = AudioplayerState.State_completed
                _currentTime.value = "00:00"
                timerJob?.cancel()
            }
        )
    }


    fun startPlayerVM() {
        _playerState.value = AudioplayerState.State_playing
        mediaPlayerInteractor.startPlayerIntr()
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerState.value == AudioplayerState.State_playing) {
                delay(300)
                _currentTime.value = mediaPlayerInteractor.getCurrentTimeIntr()
            }
        }
    }

    fun pausePlayerVM() {
        _playerState.value = AudioplayerState.State_paused
        mediaPlayerInteractor.pausePlayerIntr()
        timerJob?.cancel()
        _currentTime.postValue(mediaPlayerInteractor.getCurrentTimeIntr())
    }

    fun releasePlayerVM() {
        mediaPlayerInteractor.releasePlayerIntr()
    }

    fun playbackControl() {
        when (playerState.value) {
            AudioplayerState.State_default, AudioplayerState.State_completed -> {
                if (_previewUrl.value != currentPreviewUrl){     // доп проверка для подготовки плеера
                    _previewUrl.value?.let { preparePlayerVM(it) }
                } else {
                    startPlayerVM()
                }
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