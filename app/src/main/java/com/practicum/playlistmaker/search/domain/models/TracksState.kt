package com.practicum.playlistmaker.search.domain.models

sealed interface TracksState {

    object Loading : TracksState
    object EmptyAll : TracksState


    data class Content(
        val trackL: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: String
    ) : TracksState

    data class EmptyList(
        val message: String
    ) : TracksState

    data object EmptyInput : TracksState
}