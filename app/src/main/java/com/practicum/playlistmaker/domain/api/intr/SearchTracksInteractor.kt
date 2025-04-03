package com.practicum.playlistmaker.domain.api.intr

import com.practicum.playlistmaker.domain.models.Track

interface SearchTracksInteractor {
    fun searchTracksIntr(text: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(tracks: List<Track>?)
    }
}