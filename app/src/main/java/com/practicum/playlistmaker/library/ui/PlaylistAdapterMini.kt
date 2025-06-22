package com.practicum.playlistmaker.library.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistMiniItemBinding
import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistAdapterMini(var playlists: List<Playlist>) :
    RecyclerView.Adapter<PlaylistHolderMini>() {

    var onClickPl: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolderMini {
        Log.d("PlaylistAdapterMini", "onCreateViewHolder called")
        val layoutInspector = LayoutInflater.from(parent.context)
        val view = PlaylistMiniItemBinding.inflate(layoutInspector, parent, false)
        return PlaylistHolderMini(view)
    }

    override fun onBindViewHolder(holder: PlaylistHolderMini, position: Int) {
        Log.d("PlaylistAdapterMini", "onBindViewHolder called for position $position")
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onClickPl?.invoke(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        Log.d("PlaylistAdapterMini", "getItemCount called, returning: ${playlists.size}")
        return playlists.size
    }

    fun updateData(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }
}