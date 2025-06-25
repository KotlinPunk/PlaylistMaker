package com.practicum.playlistmaker.library.domain.models

sealed interface InfoOfPlaylistState {

    data class Content(
        val playlist: Playlist?,
    ) : InfoOfPlaylistState

    data class Error(
        val message: Throwable
    ) : InfoOfPlaylistState
}



