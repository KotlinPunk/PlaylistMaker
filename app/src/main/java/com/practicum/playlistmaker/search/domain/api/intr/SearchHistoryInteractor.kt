package com.practicum.playlistmaker.search.domain.api.intr

import com.practicum.playlistmaker.search.data.models.TrackData
import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun getTrackHistoryIntr(): List<Track>
    fun addTrackToHistoryIntr(track: TrackData)
    fun clearTrackHistoryIntr()
}