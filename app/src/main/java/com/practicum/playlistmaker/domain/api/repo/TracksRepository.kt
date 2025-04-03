package com.practicum.playlistmaker.domain.api.repo

import com.practicum.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracksRepo(text: String): List<Track>?
    fun getTrackHistoryRepo(): List<Track>
    fun addTrackToHistoryRepo(track: Track)
    fun clearTrackHistoryRepo()
}