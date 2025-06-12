package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.intr.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.api.repo.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors
import kotlinx.coroutines.flow.map

class SearchTracksInteractorImpl(private val repository: TracksRepository) :
    SearchTracksInteractor {

    override fun searchTracksIntr(text: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracksRepo(text).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}