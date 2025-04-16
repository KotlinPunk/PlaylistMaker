package com.practicum.playlistmaker.library.viewmodel

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface PlaylistFragmentState {
    data class Content(
        val playlistTracks: List<List<Track>>
    ) : PlaylistFragmentState

    data class Error(
        val message: String
    ) : PlaylistFragmentState
}