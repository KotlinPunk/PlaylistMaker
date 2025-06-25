package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.search.data.db.entity.PlaylistAndTracksEntity
import com.practicum.playlistmaker.search.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId") //
    suspend fun getPlaylist(playlistId: Long?): PlaylistEntity?

    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("UPDATE playlist_table SET trackIds = :updateTracksIds, trackCount = :updateTrackCount WHERE playlistId = :id")
    suspend fun updateTracksList(updateTracksIds: String, updateTrackCount: Int, id: Long?)

    @Query("DELETE FROM playlist_table WHERE playlistId = :id")
    suspend fun deletePlaylist(id: Long?)

    @Query("UPDATE playlist_table SET playlistName =:namePl, playlistDescription =:descriptionPl, playlistCoverPath =:imagePl WHERE playlistId = :idPl")
    suspend fun editPlaylist(idPl: Long?, namePl: String, descriptionPl: String, imagePl: String?)

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Long): PlaylistAndTracksEntity?

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist_tracks_table WHERE trackId IN (:trackIds)")
    suspend fun deleteTracksFromPlaylist(trackIds: List<Long>)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)
}
