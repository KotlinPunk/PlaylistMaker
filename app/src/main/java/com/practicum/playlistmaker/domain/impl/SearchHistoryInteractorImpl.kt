package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.intr.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.repo.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

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