package com.practicum.playlistmaker.search.domain.api.intr

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchTracksInteractor {
    fun searchTracksIntr(text: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(tracks: List<Track>?, errorMessage: String?)
    }
}