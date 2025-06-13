package com.practicum.playlistmaker.search.domain.api.intr

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchTracksInteractor {
    fun searchTracksIntr(text: String): Flow<Pair<List<Track>?, String?>>
}