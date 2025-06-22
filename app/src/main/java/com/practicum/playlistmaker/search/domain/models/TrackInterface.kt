package com.practicum.playlistmaker.search.domain.models

interface TrackInterface {
    val trackName: String
    val artistName: String
    val trackTimeMillis: Long
    val artworkUrl100: String
    val trackId: Long
    val collectionName: String?
    val releaseDate: String
    val primaryGenreName: String
    val country: String
    val previewUrl: String?

    fun getCoverArtwork(): String
    fun getTimeTrack(): String
    fun getYearTrack(): String

    fun toTrack(): Track // Метод для преобразования в доменный объект
}