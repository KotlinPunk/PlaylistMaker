package com.practicum.playlistmaker.library.domain.api.repo

import com.practicum.playlistmaker.search.data.db.entity.PlaylistAndTracksEntity
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryDbRepository {

    suspend fun getTracksFromFavoriteRepo(): Flow<List<Track>>
    suspend fun insertTrackToFavoriteRepo(track: Track)
    suspend fun deleteTrackFromFavoriteRepo(track: Track)
    suspend fun isTrackInFavorites(trackId: Long): Boolean
    suspend fun getPlaylistTotalDurationRepo(playlistName: String): Flow<Long?>
    suspend fun getTracksInPlaylistRepo(playlistName: String): Flow<List<Track>>
}