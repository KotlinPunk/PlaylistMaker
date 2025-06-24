package com.practicum.playlistmaker.library.domain.api.intr

import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun insertPlaylistIntr(playlist: Playlist): Long
    suspend fun getPlaylistIntr(playlistId: Long?): Playlist?
    fun getAllPlaylistsIntr(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylistIntr(track: Track, playlist: Playlist)

    suspend fun deleteTrackFromAnyListIntr(track: Track, playlist: Playlist?)

    suspend fun deletePlaylistIntr(playlistId: Long?)

    suspend fun editPlaylistIntr(
        idPl: Long?,
        namePl: String,
        descriptionPl: String,
        imagePl: String?
    )
}