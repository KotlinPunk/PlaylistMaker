package com.practicum.playlistmaker.library.domain.models

data class Playlist(
    val playlistId: Long?,
    val playlistName: String,
    val playlistDescription: String,
    val playlistCoverPath: String?,
    val trackIds: String?,
    val trackCount: Int?,
    val totalDuration: Long?
)
