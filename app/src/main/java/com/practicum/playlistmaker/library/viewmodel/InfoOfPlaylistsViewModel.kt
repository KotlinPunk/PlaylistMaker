package com.practicum.playlistmaker.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.practicum.playlistmaker.library.domain.api.intr.LibraryDbInteractor
import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.FavoriteFragmentState
import com.practicum.playlistmaker.library.domain.models.InfoOfPlaylistState
import com.practicum.playlistmaker.player.domain.models.PlaylistStateInPlayer
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoOfPlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val libraryDbInteractor: LibraryDbInteractor,
    private val gson: Gson = Gson()
) : ViewModel() {

    private val _playlistInfo = MutableLiveData<InfoOfPlaylistState>()
    val playlistInfo: LiveData<InfoOfPlaylistState> = _playlistInfo

    private val _playlistDataInfo = MutableLiveData<InfoOfPlaylistState>()
    val playlistDataInfo: LiveData<InfoOfPlaylistState> = _playlistDataInfo

    private val _stateLiveTrackData =
        MutableLiveData<FavoriteFragmentState>()
    val stateLiveTrackData: LiveData<FavoriteFragmentState> = _stateLiveTrackData

    private val _statePLInPlayer = MutableLiveData<PlaylistStateInPlayer>()
    val statePLInPlayer: LiveData<PlaylistStateInPlayer> = _statePLInPlayer

    private val _totalDuration = MutableStateFlow<Long?>(null)
    val totalDuration: StateFlow<Long?> = _totalDuration

    private var currentPlaylistId: Long? = null


    fun deletePlaylist(playlistId: Long?) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistIntr(playlistId)
            try {
                playlistInteractor.deletePlaylistIntr(playlistId)
                Log.d("InfoOfPlaylistsViewModel", "Playlist deleted successfully")
            } catch (e: Exception) {
                Log.e("InfoOfPlaylistsViewModel", "Error deleting playlist", e)
            }
        }
    }


    fun deleteTrackToPlaylist(track: Track, playlistId: Long?) {

        viewModelScope.launch {
            var playlist = playlistInteractor.getPlaylistIntr(playlistId)
            _playlistInfo.postValue(InfoOfPlaylistState.Content(playlist))

            val isInPlaylist = withContext(Dispatchers.IO) {
                val trackIds = playlist?.trackIds?.fromJson(gson) ?: emptyList()
                trackIds.contains(track.trackId)
            }
            if (isInPlaylist) {
                playlistInteractor.deleteTrackFromAnyListIntr(track, playlist)
                _statePLInPlayer.postValue(
                    PlaylistStateInPlayer.AddedToPlaylist(
                        playlist?.playlistName
                            ?: ""
                    )
                )
            } else {
                _statePLInPlayer.postValue(
                    PlaylistStateInPlayer.PresentInPlaylist(
                        playlist?.playlistName ?: ""
                    )
                )
            }
        }
    }

    private fun String?.fromJson(gson: Gson): List<Long> =
        this?.let { gson.fromJson(it, Array<Long>::class.java)?.toList() } ?: emptyList()


    fun loadPlaylistData(playlistId: Long?) {
        currentPlaylistId = playlistId
        viewModelScope.launch {
            try {
                if (playlistId != -1L) {
                    var playlist = playlistInteractor.getPlaylistIntr(playlistId)
                    _playlistInfo.postValue(InfoOfPlaylistState.Content(playlist))
                    val trackIdsString = playlist?.playlistName

                    Log.d("ViewModel", "trackIdsString: $trackIdsString")
                    libraryDbInteractor.getPlaylistTotalDurationIntr(trackIdsString.toString())
                        .collect { totalDuration ->
                            _totalDuration.value = totalDuration
                            Log.d("ViewModel", "totalDuration from interactor: $totalDuration")
                            _playlistInfo.postValue(
                                InfoOfPlaylistState.Content(
                                    playlist?.copy(
                                        totalDuration = totalDuration
                                    )
                                )
                            )
                        }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error loading playlist data", e)
                _playlistInfo.postValue(InfoOfPlaylistState.Error(e))
            }
        }
    }

    fun refreshPlaylistData() {
        currentPlaylistId?.let { playlistId ->
            Log.d(
                "InfoOfPlaylistsViewModel",
                "Refreshing playlist data for ID: $playlistId"
            )
            viewModelScope.launch {
                try {
                    // Get fresh data from database
                    val freshPlaylist = playlistInteractor.getPlaylistIntr(playlistId)
                    if (freshPlaylist != null) {
                        Log.d(
                            "InfoOfPlaylistsViewModel",
                            "Fresh playlist data loaded: ${freshPlaylist.playlistName}"
                        )
                        _playlistInfo.postValue(InfoOfPlaylistState.Content(freshPlaylist))
                        // Also refresh track data
                        fillTrackData(playlistId)
                    } else {
                        Log.w(
                            "InfoOfPlaylistsViewModel",
                            "Playlist not found in database"
                        )
                    }
                } catch (e: Exception) {
                    Log.e(
                        "InfoOfPlaylistsViewModel",
                        "Error refreshing playlist data",
                        e
                    )
                }
            }
        }
    }

    // подписываем на изменения ДБ для текущего плейлиста
    fun subscribeToPlaylistChanges() {
        viewModelScope.launch {
            try {
                playlistInteractor.getAllPlaylistsIntr().collect { playlists ->
                    // находим текущий плейлист и обновляем список
                    currentPlaylistId?.let { playlistId ->
                        val updatedPlaylist = playlists.find { it.playlistId == playlistId }
                        if (updatedPlaylist != null) {
                            Log.d(
                                "InfoOfPlaylistsViewModel",
                                "Playlist updated in database: ${updatedPlaylist.playlistName}"
                            )
                            _playlistInfo.postValue(InfoOfPlaylistState.Content(updatedPlaylist))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    "InfoOfPlaylistsViewModel",
                    "Error subscribing to playlist changes",
                    e
                )
            }
        }
    }


    fun fillTrackData(playlistId: Long?) {
        viewModelScope.launch {
            try {
                if (playlistId != -1L) {
                    val playlist = playlistInteractor.getPlaylistIntr(playlistId)
                    _playlistInfo.postValue(InfoOfPlaylistState.Content(playlist))
                    val trackIdsString = playlist?.playlistName

                    trackIdsString?.let { name ->
                        libraryDbInteractor.getTrackInPlaylistIntr(name)
                            .collect { tracks ->
                                processResult(tracks)
                            }
                    } ?: run {
                        // Обработка случая, когда playlistName равен null
                        _stateLiveTrackData.value =
                            FavoriteFragmentState.Error("Playlist name is null")
                    }
                }
            } catch (e: Exception) {
                _playlistInfo.postValue(InfoOfPlaylistState.Error(e))
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteFragmentState.Error(""))
        } else {
            renderState(FavoriteFragmentState.Content(tracks))
        }
    }

    private fun renderState(state: FavoriteFragmentState) {
        _stateLiveTrackData.value =
            state // можем использовать setValue, т.к. корутина выполняется в основном потоке
    }
}
