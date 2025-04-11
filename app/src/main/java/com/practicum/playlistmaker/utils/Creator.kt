package com.practicum.playlistmaker.utils

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.player.data.impl.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.storage.SearchHistoryImpl
import com.practicum.playlistmaker.player.domain.api.intr.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.api.intr.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.intr.SearchTracksInteractor
import com.practicum.playlistmaker.settings.domain.api.intr.SettingsInteractor
import com.practicum.playlistmaker.player.domain.api.repo.MediaPlayerRepository
import com.practicum.playlistmaker.search.domain.api.repo.TracksRepository
import com.practicum.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchTracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.impl.NavigatorImpl
import com.practicum.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.api.intr.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl


class Creator(private val context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("search_history_shared_prefs", Context.MODE_PRIVATE)

    private val searchHistory = SearchHistoryImpl(sharedPrefs)

    private val tracksRepository: TracksRepository by lazy { // для поиска треков
        TracksRepositoryImpl(RetrofitNetworkClient(context), searchHistory, context)
    }

    private val settingsRepository by lazy { // для настроек светлой/тёмной темы
        SettingsRepositoryImpl(context)
    }

    private val sharingRepository by lazy {
        SharingRepositoryImpl(context, NavigatorImpl(context))
    }

    private val mediaPlayerRepository: MediaPlayerRepository by lazy { // для управления аудиоплеером
        MediaPlayerRepositoryImpl()
    }

    val searchTracksInteractor: SearchTracksInteractor by lazy {
        SearchTracksInteractorImpl(tracksRepository)
    }

    val searchHistoryInteractor: SearchHistoryInteractor by lazy {
        SearchHistoryInteractorImpl(tracksRepository)
    }

    val settingsInteractor: SettingsInteractor by lazy {
        SettingsInteractorImpl(settingsRepository)
    }

    val sharingInteractor: SharingInteractor by lazy {
        SharingInteractorImpl(sharingRepository)
    }

    val mediaPlayerInteractor: MediaPlayerInteractor by lazy {
        MediaPlayerInteractorImpl(mediaPlayerRepository)
    }
}