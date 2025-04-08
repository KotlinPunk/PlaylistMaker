package com.practicum.playlistmaker.search.domain.api.intr

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun getTrackHistoryIntr(): List<Track>
    fun addTrackToHistoryIntr(track: Track)
    fun clearTrackHistoryIntr()
}