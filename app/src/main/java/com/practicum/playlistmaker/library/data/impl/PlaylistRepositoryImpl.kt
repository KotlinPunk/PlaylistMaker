package com.practicum.playlistmaker.library.data.impl

import com.practicum.playlistmaker.library.domain.api.repo.PlaylistRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.data.PlaylistDbConvertor
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.db.entity.PlaylistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistRepository {
    override suspend fun insertPlaylistRepo(playlist: Playlist): Long {
        val plEntity = playlistToPlaylistEntity(playlist)
        return withContext(Dispatchers.IO) { appDatabase.playlistDao().insertPlaylist(plEntity) }
    }

    override suspend fun updatePlaylistRepo(playlist: Playlist) {
        val plEntity = playlistToPlaylistEntity(playlist)
        appDatabase.playlistDao().updatePlaylist(plEntity)
    }

    override fun getAllPlaylistsRepo(): Flow<List<Playlist>> =
        appDatabase.playlistDao().getAllPlaylists()
            .map { playlists ->
                playlists.map { plEntity -> playlistDbConvertor.mapToPlaylist(plEntity) }


            }

    private fun playlistToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.mapToPlaylistEntity(playlist)
    }

}