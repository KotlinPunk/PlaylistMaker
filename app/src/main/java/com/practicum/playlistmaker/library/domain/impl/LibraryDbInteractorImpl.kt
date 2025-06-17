package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.api.intr.LibraryDbInteractor
import com.practicum.playlistmaker.library.domain.api.repo.LibraryDbRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

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

    override suspend fun isTrackInFavorites(trackId: Int): Boolean {
        return libraryDbRepository.isTrackInFavorites(trackId)
    }
}