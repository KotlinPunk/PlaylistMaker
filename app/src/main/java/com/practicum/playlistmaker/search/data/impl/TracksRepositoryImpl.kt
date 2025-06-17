package com.practicum.playlistmaker.search.data.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.dto.TrackResponseDto
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.domain.api.repo.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.data.impl.storage.SearchHistoryImpl
import com.practicum.playlistmaker.search.data.models.TrackData
import com.practicum.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistoryImpl: SearchHistoryImpl,
    private val context: Context,
    private val appDatabase: AppDatabase
) : TracksRepository {

    override fun searchTracksRepo(text: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(text))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(context.getString(R.string.problems_with_connection)))
            }

            200 -> {
                with(response as TrackResponseDto) {
                    val listIdTracksFavorite = appDatabase.trackDao().getTracksIdFromFavorite()
                    val data = results.map { trackDto ->
                        val track = trackDto.toTrack()
                        val isFavorite = listIdTracksFavorite.contains(track.trackId)
                        track.copy(isFavorite = isFavorite)
                    }
                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error(context.getString(R.string.nothing_found)))
            }
        }
    }

    override fun getTrackHistoryRepo(): List<Track> {
        return searchHistoryImpl.getHistoryTrackList()
            .map { it.toTrack() } // получаем именно что список треков согласно domain
    }

    override fun addTrackToHistoryRepo(track: TrackData) {
        return searchHistoryImpl.addTrackInHistoryTrackList(track.toTrackDto()) // закладываем DTO на хранение
    }

    override fun clearTrackHistoryRepo() {
        searchHistoryImpl.clearHistoryTrackList()
    }


    // Расширения для преобразования DTO в модель и обратно
    private fun TrackDto.toTrack(): Track {
        return Track(
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            trackId = trackId,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl,
            isFavorite = isFavorite
        )
    }

    private fun TrackData.toTrackDto(): TrackDto {
        return TrackDto(
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            trackId = trackId,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl ?: "",
            isFavorite = isFavorite
        )
    }
}