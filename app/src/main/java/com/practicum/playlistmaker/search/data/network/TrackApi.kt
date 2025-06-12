package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.TrackResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApi {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): TrackResponseDto
}