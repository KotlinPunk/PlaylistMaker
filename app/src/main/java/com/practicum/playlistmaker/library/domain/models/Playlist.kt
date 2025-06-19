package com.practicum.playlistmaker.library.domain.models

data class Playlist(
    val playlistId: Long?,
    val playlistName: String?,
    val playlistDescription: String,
    val playlistCoverPath: String?,
    val trackIdsJson: String?,
    val trackCount: Int?
)
