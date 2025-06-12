package com.practicum.playlistmaker.search.domain.api.repo

import com.practicum.playlistmaker.search.data.models.TrackData
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracksRepo(text: String): Flow<Resource<List<Track>>>
    fun getTrackHistoryRepo(): List<Track>
    fun addTrackToHistoryRepo(track: TrackData)
    fun clearTrackHistoryRepo()
}