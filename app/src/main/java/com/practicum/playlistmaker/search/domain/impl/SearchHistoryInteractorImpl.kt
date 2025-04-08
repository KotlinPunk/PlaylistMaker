package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.intr.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.repo.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: TracksRepository) :
    SearchHistoryInteractor {
    override fun getTrackHistoryIntr(): List<Track> {
        return repository.getTrackHistoryRepo()
    }

    override fun addTrackToHistoryIntr(track: Track) {
        repository.addTrackToHistoryRepo(track)
    }

    override fun clearTrackHistoryIntr() {
        repository.clearTrackHistoryRepo()
    }
}