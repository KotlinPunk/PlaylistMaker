package com.practicum.playlistmaker.library.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistMiniItemBinding
import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistAdapterMini(private var playlists: List<Playlist>) :
    RecyclerView.Adapter<PlaylistHolderMini>() {

    var onClickPlaylist: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolderMini {
        Log.d("MyAdapter", "onCreateViewHolder called")
        val layoutInspector = LayoutInflater.from(parent.context)
        val view = PlaylistMiniItemBinding.inflate(layoutInspector, parent, false)
        return PlaylistHolderMini(view)
    }

    override fun onBindViewHolder(holder: PlaylistHolderMini, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onClickPlaylist?.invoke(playlists[position])
            Log.d("MyAdapter", "onBindViewHolder called for position $position")
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
        Log.d("MyAdapter", "getItemCount called, returning: ${playlists.size}")
    }

    fun updatePL(newPlaylists: List<Playlist>){
        playlists = newPlaylists
        notifyDataSetChanged()
    }
}