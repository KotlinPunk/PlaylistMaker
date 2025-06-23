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
        val trackEntity = trackToTrackEntity(track)
        appDatabase.trackDao().insertTrackToFavorite(trackEntity)
    }

    override suspend fun deleteTrackFromFavoriteRepo(track: Track) {
        val trackEntity = trackToTrackEntity(track)
        appDatabase.trackDao().deleteTrackFromFavorite(trackEntity)
    }

    override suspend fun isTrackInFavorites(trackId: Long): Boolean {
        val isInFavorites = appDatabase.trackDao().isTrackInFavorites(trackId)
        return isInFavorites
    }

    override suspend fun getPlaylistTotalDurationRepo(playlistName: String): Flow<Long?> = flow {
        appDatabase.trackDao().getTrackIdsForPlaylist(playlistName).collect { trackIdsString ->
            if (trackIdsString.isNullOrEmpty()) {
                emit(0L)                                            // плейлист пуст или его нет
            } else {
                val trackIds = trackIdsString.removeSurrounding(    // убираем префиксы и суффиксы, а именно [ и ]
                    "[",
                    "]"
                )
                    .split(",")                         // разделяем строку
                    .mapNotNull {
                        it.trim().toLongOrNull()        // убираем пробелы на всякий случай в начале и конце строки,
                    }                                   // приводим к Long, а дальше фильтруем на null
                if (trackIds.isEmpty()) {
                    emit(0L)
                } else {
                    appDatabase.trackDao().getTracksByIds(trackIds).collect { tracks ->
                        val totalDuration = tracks.sumOf { it.trackTimeMillis }
                        emit(totalDuration)
                    }
                }
            }
        }
    }

    override suspend fun getTracksInPlaylistRepo(playlistName: String): Flow<List<Track>> = flow {
        try {
            val trackIdsStringFlow: Flow<String?> = appDatabase.trackDao().getTrackIdsForPlaylist(playlistName)

            trackIdsStringFlow.collect { trackIdsString ->
                if (trackIdsString.isNullOrEmpty()) {
                    emit(emptyList())
                    return@collect
                }

                val trackIds = trackIdsString.removeSurrounding("[", "]")
                    .split(",")
                    .mapNotNull { it.trim().toLongOrNull() }

                if (trackIds.isEmpty()) {
                    emit(emptyList())
                    return@collect
                }

                val trackEntitiesFlow: Flow<List<TrackEntity>> = appDatabase.trackDao().getTracksByIds(trackIds)

                trackEntitiesFlow.collect { trackEntities ->
                    val tracks = trackEntities.map { trackDbConvertor.mapToTrack(it) }
                    emit(tracks)
                }
            }
        } catch (e: Exception) {
            Log.e("getTracksInPlaylistRepo", "Error fetching tracks", e)
            emit(emptyList())
        }
    }
    private fun trackToTrackEntity(track: Track): TrackEntity {
        return trackDbConvertor.mapToTrackEntity(track)
    }
}