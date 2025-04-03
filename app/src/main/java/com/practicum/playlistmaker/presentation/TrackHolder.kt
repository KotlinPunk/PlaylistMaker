package com.practicum.playlistmaker.presentation

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.domain.models.Track

class TrackHolder(item: View) : RecyclerView.ViewHolder(item) {
    val binding = TrackItemBinding.bind(item)
    fun bind(track: Track) = with(binding) {
        trackNameXml.text = track.trackName
        artistNameXml.text = track.artistName
        trackTimeXml.text = track.getTimeTrack()
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelOffset(R.dimen.two_dp)))
            .into(artworkXml)
    }
}