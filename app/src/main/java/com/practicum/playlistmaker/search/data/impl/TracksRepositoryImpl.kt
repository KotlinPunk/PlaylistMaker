package com.practicum.playlistmaker.search.data.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.dto.TrackResponseDto
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.domain.api.repo.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.data.impl.storage.SearchHistoryImpl
import com.practicum.playlistmaker.search.data.models.TrackData
import com.practicum.playlistmaker.utils.Resource

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistoryImpl: SearchHistoryImpl,
    private val context: Context
) : TracksRepository {

    override fun searchTracksRepo(text: String): Resource<List<Track>> {
        val responce = networkClient.doRequest(TrackSearchRequest(text))
        return when (responce.resultCode){
            -1 -> {
                Resource.Error(context.getString(R.string.problems_with_connection))
            }
            200 -> {
                Resource.Success(((responce as TrackResponseDto).results.map { trackDto -> trackDto.toTrack() }))
            }
            else -> {
                Resource.Error(context.getString(R.string.nothing_found))
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
            previewUrl = previewUrl
        )
    }

    private fun Track.toTrackDto(): TrackDto {
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
            previewUrl = previewUrl ?: ""
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
            previewUrl = previewUrl ?: ""
        )
    }
}