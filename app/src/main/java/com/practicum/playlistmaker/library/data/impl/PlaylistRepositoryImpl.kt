package com.practicum.playlistmaker.library.data.impl

import android.util.Log
import com.google.gson.Gson
import com.practicum.playlistmaker.library.domain.api.repo.PlaylistRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.data.PlaylistDbConvertor
import com.practicum.playlistmaker.search.data.TrackDbConvertor
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.db.entity.PlaylistAndTracksEntity
import com.practicum.playlistmaker.search.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

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

    override suspend fun deleteTrackFromAnyListRepo(
        track: Track,
        playlist: Playlist?
    ) {
        if (playlist == null) {
            return
        }
        val trackIds = playlist.trackIds.fromJson(gson).toMutableList()

        // Удаляем trackId из списка, если он там есть
        val isRemoved = trackIds.remove(track.trackId)
        if (!isRemoved) {
            Log.w(
                "PlaylistRepository",
                "Track ID ${track.trackId} not found in playlist ${playlist.playlistId}"
            )
        }
        // Обновляем количество треков
        val updateTrackCount = trackIds.size
        // Преобразуем обновленный список ID треков обратно в JSON строку
        val updateTrackIdsJson = trackIds.toJson(gson)

        // Обновляем плейлист в базе данных
        val playlistId = playlist.playlistId
        if (playlistId == null) {
            Log.e("PlaylistRepository", "Playlist ID is null. Cannot update playlist.")
            return
        }
        appDatabase.playlistDao().updateTracksList(
            updateTracksIds = updateTrackIdsJson,
            updateTrackCount = updateTrackCount,
            id = playlistId
        )

        getAllPlaylistsRepo().collect {
            checkTrackInPlaylists(track, it)
        }
    }

    private suspend fun checkTrackInPlaylists(track: Track, playlists: List<Playlist>) {
        var check = 0
        playlists.forEach { playlist ->
            val delTrackIds = playlist.trackIds.fromJson(gson).toMutableList()
            if (delTrackIds.contains(track.trackId.toLong())) {
                check++
            }
        }
        if (check == 0) {
            val chosenTrackEntity = playlistDbConvertor.mapToPlaylistAndTracks(track)
            appDatabase.playlistAndTracksDao().deleteTrackFromAnyPlaylist(chosenTrackEntity)
        }
    }


    override suspend fun deletePlaylistRepo(playlist: Playlist) {
        val trackIds = playlist.trackIds?.fromJson(gson)?.toList() ?: emptyList()

        appDatabase.playlistDao().deletePlaylist(playlistDbConvertor.mapToPlaylistEntity(playlist))

        // Удаляем треки из playlist_tracks_table, только если они не используются в других плейлистах
        trackIds.forEach { trackId ->
            val track = appDatabase.playlistDao().getTrackById(trackId)

            if (track != null) {
                // Проверяем, используется ли трек в других плейлистах
                val inOtherPlaylists = appDatabase.playlistDao().getAllPlaylists().first()
                    .any { otherPlaylist ->
                        otherPlaylist.trackIds?.fromJson(gson)?.contains(trackId) == true && otherPlaylist.playlistId != playlist.playlistId
                    }

                if (!inOtherPlaylists) {
                    // Если трек не используется в других плейлистах, удаляем его
                    appDatabase.playlistAndTracksDao().deleteTrackFromAnyPlaylist(track)
                }
            }
        }
    }


    override suspend fun editPlaylistRepo(
        idPl: Long?,
        namePl: String,
        descriptionPl: String,
        imagePl: String?
    ) {
        appDatabase.playlistDao().editPlaylist(idPl, namePl, descriptionPl, imagePl)
    }

    override suspend fun removeTrackFromPlaylistRepo(
        trackId: Long,
        playlist: Playlist?
    ) {
        val convertPlaylist = playlistDbConvertor.mapToPlaylistEntity(playlist)

        // 1. Получить текущий список trackIds из playlist.trackIds
        val trackIds = convertPlaylist.trackIds?.fromJson(gson)?.toMutableList() ?: mutableListOf()

        // 2. Удалить trackId из списка
        trackIds.remove(trackId)

        // 3. Обновить trackCount
        val newTrackCount = trackIds.size

        // 4. Преобразовать обновленный список trackIds обратно в JSON
        val updatedTrackIdsJson = trackIds.toJson(gson)

        // 5. Обновить PlaylistEntity в базе данных
        val updatedPlaylist = convertPlaylist.copy(
            trackIds = updatedTrackIdsJson,
            trackCount = newTrackCount
        )

        appDatabase.playlistDao().updatePlaylist(updatedPlaylist)
    }


    // Функция для преобразования списка Track ID в JSON строку
    private fun List<Long>.toJson(gson: Gson): String = gson.toJson(this)

    // Функция для преобразования JSON строки в список Track ID
    private fun String?.fromJson(gson: Gson): List<Long> =
        this?.let { gson.fromJson(it, Array<Long>::class.java)?.toList() } ?: emptyList()
}