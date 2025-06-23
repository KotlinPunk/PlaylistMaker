package com.practicum.playlistmaker.library.domain.impl

import android.util.Log
import com.practicum.playlistmaker.library.domain.api.intr.LibraryDbInteractor
import com.practicum.playlistmaker.library.domain.api.repo.LibraryDbRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LibraryDbInteractorImpl(private val libraryDbRepository: LibraryDbRepository) :
    LibraryDbInteractor {

    override suspend fun getTracksFromFavoriteIntr(): Flow<List<Track>> {
        return libraryDbRepository.getTracksFromFavoriteRepo()
    }

    override suspend fun insertTrackToFavoriteIntr(track: Track) {
        return libraryDbRepository.insertTrackToFavoriteRepo(track)
    }

    override suspend fun deleteTrackFromFavoriteIntr(track: Track) {
        return libraryDbRepository.deleteTrackFromFavoriteRepo(track)
    }

    override suspend fun isTrackInFavorites(trackId: Long): Boolean {
        return libraryDbRepository.isTrackInFavorites(trackId)
    }

    override suspend fun getPlaylistTotalDurationIntr(playlistName: String): Flow<Long?> {
        Log.d("LibraryDbInteractor", "getPlaylistTotalDurationIntr called with playlistName: $playlistName")
        return libraryDbRepository.getPlaylistTotalDurationRepo(playlistName)
    }

    override suspend fun getTrackInPlaylistIntr(playlistName: String): Flow<List<Track>> {
        return libraryDbRepository.getTrackInPlaylistRepo(playlistName)
    }

}