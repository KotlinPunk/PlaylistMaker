package com.practicum.playlistmaker.library.domain.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed class InfoOfPlaylistState {

    data class Content(
        val playlist: Playlist?,
    ) : InfoOfPlaylistState()

    data class Error(
        val message: Throwable
    ) : InfoOfPlaylistState()
}



