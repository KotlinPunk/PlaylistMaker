package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.data.db.entity.PlaylistAndTracksEntity
import com.practicum.playlistmaker.search.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.search.domain.models.Track

class PlaylistDbConvertor {

    fun mapToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId ?: 0,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistCoverPath,
            playlist.trackIds ?: "[]",
            playlist.trackCount ?: 0
        )
    }

    fun mapToPlaylist(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.playlistCoverPath,
            playlistEntity.trackIds,
            playlistEntity.trackCount
        )
    }

    fun mapToPlaylistAndTracks(track: Track): PlaylistAndTracksEntity{
        return PlaylistAndTracksEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
        )
    }
}