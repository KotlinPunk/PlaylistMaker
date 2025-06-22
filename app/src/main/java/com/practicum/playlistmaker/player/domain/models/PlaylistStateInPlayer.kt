package com.practicum.playlistmaker.player.domain.models

sealed interface PlaylistStateInPlayer {
    class PresentInPlaylist(val namePL: String) : PlaylistStateInPlayer
    class AddedToPlaylist(val namePL: String) : PlaylistStateInPlayer
}