package com.practicum.playlistmaker.library.data.impl

import com.google.gson.Gson
import com.practicum.playlistmaker.library.domain.api.repo.PlaylistRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.data.PlaylistDbConvertor
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.db.entity.PlaylistAndTracksEntity
import com.practicum.playlistmaker.search.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val gson: Gson = Gson()
) : PlaylistRepository {
    override suspend fun insertPlaylistRepo(playlist: Playlist): Long {
        val plEntity = playlistToPlaylistEntity(playlist)
        return appDatabase.playlistDao().insertPlaylist(plEntity)
    }

    override suspend fun getPlaylistRepo(playlistId: Long?): Playlist? {
        val playlistEntity = appDatabase.playlistDao().getPlaylist(playlistId)
        return playlistEntity?.let { playlistDbConvertor.mapToPlaylist(it) }
    }

    override fun getAllPlaylistsRepo(): Flow<List<Playlist>> =
        appDatabase.playlistDao().getAllPlaylists()
            .map { playlists ->
                playlists.map { plEntity -> playlistDbConvertor.mapToPlaylist(plEntity) }
            }

    override suspend fun addTrackToPlaylistRepo(
        track: Track,
        playlist: Playlist
    ) {
        val playlistAndTracksEntity = toPlaylistAndTracksEntity(track)

        val trackIds = playlist.trackIds?.fromJson(gson) ?: emptyList()
        if (trackIds.contains(track.trackId)) return // Трек уже есть - выходим

        val updateTracksIds = trackIds + track.trackId

        val updatedTracksIds = updateTracksIds.toJson(gson)
        val updatePlaylistTrackCount = playlist.trackCount?.plus(1) ?: 1

        val playlistId = playlist.playlistId

        appDatabase.playlistDao().updateTracksList(
            updateTracksIds = updatedTracksIds,
            updateTrackCount = updatePlaylistTrackCount,
            id = playlistId
        )
        appDatabase.playlistAndTracksDao().insertTrackToPlaylist(playlistAndTracksEntity)

    }

    private fun playlistToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.mapToPlaylistEntity(playlist)
    }

    private fun toPlaylistAndTracksEntity(track: Track): PlaylistAndTracksEntity {
        return playlistDbConvertor.mapToPlaylistAndTracks(track)
    }

    // Функция для преобразования списка Track ID в JSON строку
    private fun List<Long>.toJson(gson: Gson): String = gson.toJson(this)

    // Функция для преобразования JSON строки в список Track ID
    private fun String?.fromJson(gson: Gson): List<Long> =
        this?.let { gson.fromJson(it, Array<Long>::class.java)?.toList() } ?: emptyList()
}