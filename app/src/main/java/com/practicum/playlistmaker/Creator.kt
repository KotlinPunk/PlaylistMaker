package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.data.impl.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.impl.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.impl.storage.SearchHistoryImpl
import com.practicum.playlistmaker.domain.api.intr.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.api.intr.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.intr.SearchTracksInteractor
import com.practicum.playlistmaker.domain.api.intr.SettingsInteractor
import com.practicum.playlistmaker.domain.api.repo.MediaPlayerRepository
import com.practicum.playlistmaker.domain.api.repo.ThemeRepository
import com.practicum.playlistmaker.domain.api.repo.TracksRepository
import com.practicum.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl


class Creator(private val context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("search_history_shared_prefs", Context.MODE_PRIVATE)

    private val searchHistory = SearchHistoryImpl(sharedPrefs)

    private val tracksRepository: TracksRepository by lazy {
        TracksRepositoryImpl(RetrofitNetworkClient(), searchHistory)
    }

    private val themeRepository by lazy {
        ThemeRepositoryImpl(context)
    }

    private val mediaPlayerRepository: MediaPlayerRepository by lazy {
        MediaPlayerRepositoryImpl()
    }

    val searchTracksInteractor: SearchTracksInteractor by lazy {
        SearchTracksInteractorImpl(tracksRepository)
    }

    val searchHistoryInteractor: SearchHistoryInteractor by lazy {
        SearchHistoryInteractorImpl(tracksRepository)
    }

    val settingsInteractor: SettingsInteractor by lazy {
        SettingsInteractorImpl(context, themeRepository)
    }

    val mediaPlayerInteractor: MediaPlayerInteractor by lazy {
        MediaPlayerInteractorImpl(mediaPlayerRepository)
    }
}