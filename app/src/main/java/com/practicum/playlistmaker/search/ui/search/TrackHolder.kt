package com.practicum.playlistmaker.search.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TrackHolder(private val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {
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