package com.practicum.playlistmaker.search.data.models

import android.os.Parcelable
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TrackInterface
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class TrackData(
    override val trackName: String,
    override val artistName: String,
    override val trackTimeMillis: Long,
    override val artworkUrl100: String,
    override val trackId: Int,
    override val collectionName: String?,
    override val releaseDate: String,
    override val primaryGenreName: String,
    override val country: String,
    override val previewUrl: String?,
    var isFavorite: Boolean
) : Parcelable, TrackInterface {

    override fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    override fun getTimeTrack(): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    override fun getYearTrack() =
        releaseDate.substringBefore("-") //метод для получения подстроки расположенной перед первым вхождением символа в строке releaseDate, было 2022-01-01, будет 2022

    override fun toTrack(): Track {
        return Track(
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            artworkUrl100 = this.artworkUrl100,
            trackId = this.trackId,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            previewUrl = this.previewUrl
        )
    }
}