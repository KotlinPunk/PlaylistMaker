package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BrokerPlaylistTrackDao {

    @Query("DELETE FROM broker_playlist_track_table WHERE playlistId = :playlistId")
    suspend fun deletePlaylistTrackEntity(playlistId: Long?)

}