package com.practicum.playlistmaker.library.domain.api.intr

import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun insertPlaylistIntr(playlist: Playlist) : Long
    /*suspend fun updatePlaylistRepo(playlist: Playlist)*/
    suspend fun getAllPlaylistsIntr(): Flow<List<Playlist>>
}