package com.practicum.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistAdapter(private var playlists: List<Playlist>) :
    RecyclerView.Adapter<PlaylistHolder>() {

    var onClickPL: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        val view = PlaylistItemBinding.inflate(layoutInspector, parent, false)
        return PlaylistHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onClickPL?.invoke(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}