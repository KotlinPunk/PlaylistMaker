package com.practicum.playlistmaker.search.data.dto

class TrackResponseDto(
    val resultCount: Int,
    val results: ArrayList<TrackDto>
) : Response()