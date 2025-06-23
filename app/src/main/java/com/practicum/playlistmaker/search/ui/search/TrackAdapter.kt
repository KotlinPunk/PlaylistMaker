package com.practicum.playlistmaker.search.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

class TrackAdapter(private var trackList: List<Track>) :
    RecyclerView.Adapter<TrackHolder>() {

    var onClickTrack: ((Track) -> Unit)? = null
    var onLongClickTrack: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        val view = TrackItemBinding.inflate(layoutInspector, parent, false)
        return TrackHolder(view)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            onClickTrack?.invoke(trackList[position])
        }
        holder.itemView.setOnLongClickListener {
            onLongClickTrack?.invoke(trackList[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun updateData(newTrackList: List<Track>) {
        trackList = newTrackList
        notifyDataSetChanged()
    }
}