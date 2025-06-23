package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.search.data.db.entity.PlaylistAndTracksEntity
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistAndTracksDao {

    @Insert(entity = PlaylistAndTracksEntity:: class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackToPlaylist(track: PlaylistAndTracksEntity)

    @Delete(entity = PlaylistAndTracksEntity::class)
    suspend fun deleteTrackEntity(track: PlaylistAndTracksEntity)

    @Query("SELECT * FROM playlist_tracks_table")
    suspend fun getTracksInPlaylists(): List<PlaylistAndTracksEntity>
}