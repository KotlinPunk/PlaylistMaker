package com.practicum.playlistmaker.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.search.data.db.entity.BrokerPlaylistTrackEntity
import com.practicum.playlistmaker.search.data.db.dao.PlaylistAndTracksDao
import com.practicum.playlistmaker.search.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.search.data.db.dao.TrackDao
import com.practicum.playlistmaker.search.data.db.entity.PlaylistAndTracksEntity
import com.practicum.playlistmaker.search.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity

@Database(
    version = 16,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistAndTracksEntity::class, BrokerPlaylistTrackEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistAndTracksDao(): PlaylistAndTracksDao

}