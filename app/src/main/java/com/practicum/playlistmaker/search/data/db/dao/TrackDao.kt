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
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToFavorite(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrackFromFavorite(track: TrackEntity)

    @Query("SELECT * FROM track_table ORDER BY lastAdded DESC")
    suspend fun getTracksFromFavorite(): List<TrackEntity>

    @Query("SELECT trackId FROM track_table")
    suspend fun getTracksIdFromFavorite(): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE trackId = :trackId)") // интересует только лишь наличие хотя бы одной строки
    suspend fun isTrackInFavorites(trackId: Long): Boolean // true - строка есть, иначе false


    @Query("SELECT trackIds FROM playlist_table WHERE playlistName = :playlistName")
    fun getTrackIdsForPlaylist(playlistName: String): Flow<String?>

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId IN (:trackIds) ORDER BY lastAdded DESC")
    fun getTracksByIds(trackIds: List<Long>): Flow<List<PlaylistAndTracksEntity>>

   /* @Query("SELECT EXISTS(SELECT 1 FROM playlist_tracks_table WHERE trackId = :trackId)") // интересует только лишь наличие хотя бы одной строки
    suspend fun isTrackHaveInAnyPlaylist(trackId: Long): Boolean // true - строка есть, иначе false*/
}