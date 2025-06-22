package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.search.data.db.entity.PlaylistAndTracksEntity

@Dao
interface PlaylistAndTracksDao {

    @Insert(entity = PlaylistAndTracksEntity:: class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackToPlaylist(track: PlaylistAndTracksEntity)
}