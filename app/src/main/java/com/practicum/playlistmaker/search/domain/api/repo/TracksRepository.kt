package com.practicum.playlistmaker.search.domain.api.repo

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.Resource

interface TracksRepository {
    fun searchTracksRepo(text: String): Resource<List<Track>>
    fun getTrackHistoryRepo(): List<Track>
    fun addTrackToHistoryRepo(track: Track)
    fun clearTrackHistoryRepo()
}