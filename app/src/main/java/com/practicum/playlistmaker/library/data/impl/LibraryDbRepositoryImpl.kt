package com.practicum.playlistmaker.library.data.impl

import android.util.Log
import com.practicum.playlistmaker.library.domain.api.repo.LibraryDbRepository
import com.practicum.playlistmaker.search.data.TrackDbConvertor
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LibraryDbRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : LibraryDbRepository {

    override suspend fun getTracksFromFavoriteRepo(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracksFromFavorite()
        emit(tracks.map { trackEnt -> trackDbConvertor.mapToTrack(trackEnt) })
    }

    override suspend fun insertTrackToFavoriteRepo(track: Track) {
        Log.d("LibraryDbRepositoryImpl", "Inserting track: ${track.trackId}")
        val trackEntity = trackToTrackEntity(track)
        appDatabase.trackDao().insertTrackToFavorite(trackEntity)
        Log.d("LibraryDbRepositoryImpl", "Track inserted: ${track.trackId}")
    }

    override suspend fun deleteTrackFromFavoriteRepo(track: Track) {
        Log.d("LibraryDbRepositoryImpl", "Deleting track: ${track.trackId}")
        val trackEntity = trackToTrackEntity(track)
        appDatabase.trackDao().deleteTrackFromFavorite(trackEntity)
        Log.d("LibraryDbRepositoryImpl", "Track deleted: ${track.trackId}")
    }

    override suspend fun isTrackInFavorites(trackId: Int): Boolean {
        val isInFavorites = appDatabase.trackDao().isTrackInFavorites(trackId)
        Log.d("LibraryDbRepositoryImpl", "isTrackInFavorites called for trackId: $trackId, result: $isInFavorites")
        return isInFavorites
    }

    private fun trackToTrackEntity(track: Track): TrackEntity {
        return trackDbConvertor.mapToTrackEntity(track)
    }
}