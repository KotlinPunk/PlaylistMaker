package com.practicum.playlistmaker.library.domain.api.repo

import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {

    suspend fun insertPlaylistRepo(playlist: Playlist): Long
    suspend fun getPlaylistRepo(playlistId: Long?): Playlist?
    fun getAllPlaylistsRepo(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylistRepo(track: Track, playlist: Playlist)

    suspend fun deleteTrackFromAnyListRepo(track: Track, playlist: Playlist?)

    suspend fun deletePlaylistRepo(playlistId: Long?)

    suspend fun editPlaylistRepo(
        idPl: Long?,
        namePl: String,
        descriptionPl: String,
        imagePl: String?
    )
}
