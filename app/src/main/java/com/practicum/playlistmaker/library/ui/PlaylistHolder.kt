package com.practicum.playlistmaker.library.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) = with(binding) {
        itemNamePL.text = playlist.playlistName
        itemCountPL.text = "${playlist.trackCount} ${endingCount(playlist.trackCount)}"
        Glide.with(itemView)
            .load(playlist.playlistCoverPath ?: R.drawable.ic_placeholder)
            .placeholder(R.drawable.ic_placeholder)
            .transform(CenterCrop(),RoundedCorners(itemView.resources.getDimensionPixelOffset(R.dimen.eight_dp)))
            .into(itemImagePL)
    }

    private fun endingCount(trackCount: Int?): String {
        if (trackCount == null) return "треков"
        return when {
            trackCount % 100 in 11..14 -> "треков"
            trackCount % 10 == 1 -> "трек"
            trackCount % 10 in 2..4 -> "трека"
            else -> "треков"
        }
    }
}