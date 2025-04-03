package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.intr.SearchTracksInteractor
import com.practicum.playlistmaker.domain.api.repo.TracksRepository
import java.util.concurrent.Executors

class SearchTracksInteractorImpl(private val repository: TracksRepository) :
    SearchTracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracksIntr(text: String, consumer: SearchTracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracksRepo(text))
        }
    }
}