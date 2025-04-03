package com.practicum.playlistmaker.domain.api.intr

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun getTrackHistoryIntr(): List<Track>
    fun addTrackToHistoryIntr(track: Track)
    fun clearTrackHistoryIntr()
}