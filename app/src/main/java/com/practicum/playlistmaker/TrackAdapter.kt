package com.practicum.playlistmaker

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.internal.ViewUtils.dpToPx
import com.practicum.playlistmaker.databinding.TrackItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(private val trackList: ArrayList<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackHolder>() {

    var onClickTrack: ((Track) -> Unit)? = null

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackHolder(view)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            onClickTrack?.invoke(trackList[position])
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}