package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BrokerPlaylistTrackDao {

    @Query("SELECT EXISTS(SELECT 1 FROM broker_playlist_track_table WHERE trackId = :trackId)")
    suspend fun isTrackHaveAnyPlaylist(trackId: Long?): Boolean

    @Query("DELETE FROM broker_playlist_track_table WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deletePlaylistTrackEntity(playlistId: Long?, trackId: Long?) : Int

    @Query("DELETE FROM broker_playlist_track_table WHERE playlistId = :playlistId")
    suspend fun deleteAllPlaylistTrackEntities(playlistId: Long?) : Int

}