package com.practicum.playlistmaker.player.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.player.domain.api.intr.AudioPlayerInteractor
import com.practicum.playlistmaker.search.domain.models.Track

class AudioPlayerViewModel(application: Application, private val mediaPlayerInteractor: AudioPlayerInteractor) : AndroidViewModel(application) {

    private val _track = MutableLiveData<Track>()
    val track: LiveData<Track> = _track

    private val _isPlaying = MutableLiveData<Boolean>(false) // слушаем статус проигрывания
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _currentTime = MutableLiveData<String>(BY_ZEROS) // слушаем время проигрывания
    val currentTime: LiveData<String> = _currentTime


    //слушаем состояние проигрывателя: готов он или нет
    private val _isPrepared = MutableLiveData<Boolean>(false)
    val isPrepared: LiveData<Boolean> = _isPrepared

    fun setTrack(track: Track) {
        _track.postValue(track)
        preparePlayerVM(track.previewUrl)
    }

    private fun preparePlayerVM(previewUrl: String?) {
        mediaPlayerInteractor.preparePlayerIntr(
            previewUrl = previewUrl,
            onPrepared = { _isPrepared.postValue(true) },
            onCompletion = {
                _isPlaying.postValue(false)
                _currentTime.postValue(BY_ZEROS)
            },
            onTimeUpdate = { time -> _currentTime.postValue(time) }
        )
    }

    fun togglePlaybackControl() { //теперь тут следим за переключением состояния. общий маркер _isPlaying
        if (_isPlaying.value == true) {
            pausePlayerVM()
        } else {
            startPlayerVM()
        }
    }


    fun startPlayerVM() {
        mediaPlayerInteractor.startPlayerIntr()
        _isPlaying.value = true // обновляем Live Data
    }

    fun pausePlayerVM() {
        mediaPlayerInteractor.pausePlayerIntr()
        _isPlaying.value = false // обновляем Live Data
    }

    fun releasePlayerVM() {
        mediaPlayerInteractor.releasePlayerIntr()
    }

    companion object {
        private const val BY_ZEROS = "0:00"
    }
}