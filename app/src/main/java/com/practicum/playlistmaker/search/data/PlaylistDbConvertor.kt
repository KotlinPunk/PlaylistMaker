package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.data.db.entity.PlaylistEntity

class PlaylistDbConvertor {

    fun mapToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId ?: 0,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistCoverPath,
            playlist.trackIdsJson ?: "[]",
            playlist.trackCount ?: 0
        )
    }

    fun mapToPlaylist(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.playlistCoverPath,
            playlistEntity.trackIdsJson,
            playlistEntity.trackCount
        )
    }
}