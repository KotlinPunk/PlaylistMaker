package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "broker_playlist_track_table")
data class BrokerPlaylistTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val playlistId: Long?,
    val trackId: Long?
)