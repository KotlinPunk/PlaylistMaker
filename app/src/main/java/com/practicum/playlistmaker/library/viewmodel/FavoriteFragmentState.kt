package com.practicum.playlistmaker.library.viewmodel

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavoriteFragmentState {

    data class Content(
        val favoriteTracks: List<Track>
    ) : FavoriteFragmentState

    data class Error(
        val message: String
    ) : FavoriteFragmentState
}