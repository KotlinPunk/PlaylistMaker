package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.api.repo.PlaylistRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository): PlaylistInteractor {
    override suspend fun insertPlaylistIntr(playlist: Playlist): Long {
        return playlistRepository.insertPlaylistRepo(playlist)
    }

    override suspend fun updatePlaylistIntr(playlist: Playlist) {
        playlistRepository.updatePlaylistRepo(playlist)
    }

    override fun getAllPlaylistsIntr(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylistsRepo()
    }
}