package com.practicum.playlistmaker.data.dto

class TrackResponseDto(
    val resultCount: Int,
    val results: ArrayList<TrackDto>
) : Response()