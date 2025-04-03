package com.practicum.playlistmaker.data.impl

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.dto.TrackResponseDto
import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.impl.storage.SearchHistoryImpl
import com.practicum.playlistmaker.domain.api.repo.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistoryImpl: SearchHistoryImpl
) : TracksRepository {

    override fun searchTracksRepo(text: String): List<Track>? {
        val responce = networkClient.doRequest(TrackSearchRequest(text))
        if (responce.resultCode == 200) {
            return (responce as TrackResponseDto).results.map { trackDto -> trackDto.toTrack() }
        } else {
            return null
        }
    }

    override fun getTrackHistoryRepo(): List<Track> {
        return searchHistoryImpl.getHistoryTrackList()
            .map { it.toTrack() } // получаем именно что список треков согласно domain
    }

    override fun addTrackToHistoryRepo(track: Track) {
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
            previewUrl = previewUrl
        )
    }
}