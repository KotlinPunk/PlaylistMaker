package com.practicum.playlistmaker.library.domain.api.intr

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryDbInteractor {
    suspend fun getTracksFromFavoriteIntr(): Flow<List<Track>>
    suspend fun insertTrackToFavoriteIntr(track: Track)
    suspend fun deleteTrackFromFavoriteIntr(track: Track)
    suspend fun isTrackInFavorites(trackId: Long): Boolean
    suspend fun getPlaylistTotalDurationIntr(playlistName: String): Flow<Long?>
    suspend fun getTracksInPlaylistIntr(playlistName: String): Flow<List<Track>>
}