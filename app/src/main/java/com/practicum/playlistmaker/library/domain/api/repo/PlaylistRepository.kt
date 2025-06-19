package com.practicum.playlistmaker.library.domain.api.repo

import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {

    suspend fun insertPlaylistRepo(playlist: Playlist): Long
    suspend fun updatePlaylistRepo(playlist: Playlist)
    fun getAllPlaylistsRepo(): Flow<List<Playlist>>
}
