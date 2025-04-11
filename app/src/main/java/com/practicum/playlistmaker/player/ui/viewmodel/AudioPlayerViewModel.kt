package com.practicum.playlistmaker.player.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.domain.api.intr.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.Creator

class AudioPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val creator: Creator by lazy { Creator(application) }
    private val mediaPlayerInteractor: MediaPlayerInteractor by lazy {
        creator.mediaPlayerInteractor
    }

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

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}