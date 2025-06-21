package com.practicum.playlistmaker.player.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.practicum.playlistmaker.library.domain.api.intr.LibraryDbInteractor
import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.domain.models.PlaylistFragmentState
import com.practicum.playlistmaker.player.domain.api.intr.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.models.AudioplayerState
import com.practicum.playlistmaker.player.domain.models.PlaylistStateInPlayer
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AudioPlayerViewModel(
    application: Application,
    private val mediaPlayerInteractor: AudioPlayerInteractor,
    private val libraryDbInteractor: LibraryDbInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val gson: Gson = Gson()
) : AndroidViewModel(application) {

    private var timerJob: Job? = null
    private var currentTrack: Track? = null //храним текущий трек

    private val _playerState = MutableLiveData<AudioplayerState>(AudioplayerState.State_default)
    val playerState: LiveData<AudioplayerState> = _playerState

    private val _currentTime = MutableLiveData<String>("00:00")
    val currentTime: LiveData<String> = _currentTime

    private val _previewUrl = MutableLiveData<String?>()
    val previewUrl: LiveData<String?> = _previewUrl

    private val _isFavorite = MutableLiveData<Boolean>(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _stateLiveDataPL = MutableLiveData<PlaylistFragmentState>()
    val stateLiveDataPL: LiveData<PlaylistFragmentState> = _stateLiveDataPL

    private val _statePLInPlayer = MutableLiveData<PlaylistStateInPlayer>()
    val statePLInPlayer: LiveData<PlaylistStateInPlayer> = _statePLInPlayer


    fun setTrack(track: Track) { // метод для установки текущего треки и инициализации _isFavorite начальным значением
        currentTrack = track.copy() // для создания новой копии Track с изменённым isFavorite
        viewModelScope.launch {
            _isFavorite.postValue(libraryDbInteractor.isTrackInFavorites(track.trackId))
        }
        _previewUrl.value = track.previewUrl
        preparePlayerVM(track.previewUrl)
    }

    fun onFavoriteClicked() {
        currentTrack?.let { track ->
            viewModelScope.launch {
                try {
                    val isCurrentlyFavorite = libraryDbInteractor.isTrackInFavorites(track.trackId)

                    if (isCurrentlyFavorite) {
                        libraryDbInteractor.deleteTrackFromFavoriteIntr(track)
                    } else {

                        libraryDbInteractor.insertTrackToFavoriteIntr(track)
                    }
                    _isFavorite.postValue(!isCurrentlyFavorite)
                } catch (e: Exception) {
                    // Обработка ошибки
                }
            }
        }
    }

    fun fillDataPL() {
        Log.d("AudioVM", "Beg")
        viewModelScope.launch {
            Log.d("AudioVM", "Ing")
            withContext(Dispatchers.IO) {
                playlistInteractor
                    .getAllPlaylistsIntr()
                    .collect { playlists -> processResult(playlists) }
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        Log.d("AudioVM", "Received playlists: ${playlists.size}")
        if (playlists.isEmpty()) {
            renderState(PlaylistFragmentState.Error(""))
        } else {
            renderState(PlaylistFragmentState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistFragmentState) {
        _stateLiveDataPL.postValue(state)
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        Log.d("AudioVM", "Beg")
        viewModelScope.launch {
            Log.d("AudioVM", "Ing")
            val isInPlaylist = withContext(Dispatchers.IO) {
                val trackIds = playlist.trackIds?.fromJson(gson) ?: emptyList()
                trackIds.contains(track.trackId)
            }
            if (!isInPlaylist) {
                playlistInteractor.addTrackToPlaylistIntr(track, playlist)
                _statePLInPlayer.postValue(PlaylistStateInPlayer.AddedToPlaylist(playlist.playlistName))
            } else {
                _statePLInPlayer.postValue(PlaylistStateInPlayer.PresentInPlaylist(playlist.playlistName))
            }
        }
    }

    private fun String?.fromJson(gson: Gson): List<Long> =
        this?.let { gson.fromJson(it, Array<Long>::class.java)?.toList() } ?: emptyList()

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
                startPlayerVM()
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
