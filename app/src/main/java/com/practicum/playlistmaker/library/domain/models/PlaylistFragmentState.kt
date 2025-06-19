package com.practicum.playlistmaker.library.domain.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface PlaylistFragmentState {
    data class Content(
        val playlistTracks: List<Playlist>
    ) : PlaylistFragmentState

    data class Error(
        val message: String
    ) : PlaylistFragmentState
}