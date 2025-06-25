package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.api.repo.PlaylistRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun insertPlaylistIntr(playlist: Playlist): Long {
        return playlistRepository.insertPlaylistRepo(playlist)
    }

    override suspend fun getPlaylistIntr(playlistId: Long?): Playlist? {
        return playlistRepository.getPlaylistRepo(playlistId)
    }

    override fun getAllPlaylistsIntr(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylistsRepo()
    }

    override suspend fun addTrackToPlaylistIntr(
        track: Track,
        playlist: Playlist
    ) {
        playlistRepository.addTrackToPlaylistRepo(track, playlist)
    }

    override suspend fun deleteTrackFromAnyListIntr(
        track: Track,
        playlist: Playlist?
    ) {
        playlistRepository.deleteTrackFromAnyListRepo(track, playlist)
    }

    override suspend fun deletePlaylistIntr(playlist: Playlist) {
        playlistRepository.deletePlaylistRepo(playlist)
    }

    override suspend fun editPlaylistIntr(
        idPl: Long?,
        namePl: String,
        descriptionPl: String,
        imagePl: String?
    ) {
        playlistRepository.editPlaylistRepo(
            idPl,
            namePl,
            descriptionPl,
            imagePl
        )
    }

    override suspend fun removeTrackFromPlaylistIntr(
        trackId: Long,
        playlist: Playlist?
    ) {
        playlistRepository.removeTrackFromPlaylistRepo(trackId, playlist)
    }
}