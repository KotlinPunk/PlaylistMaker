package com.practicum.playlistmaker.search.data.db.entity

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query


@Dao
interface BrokerPlaylistTrackDao {

    @Query("SELECT EXISTS(SELECT 1 FROM broker_playlist_track_table WHERE trackId = :trackId)")
    suspend fun isTrackHaveAnyPlaylist(trackId: Long?): Boolean

    @Query("DELETE FROM broker_playlist_track_table WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deletePlaylistTrackEntity(playlistId: Long?, trackId: Long?) : Int
}