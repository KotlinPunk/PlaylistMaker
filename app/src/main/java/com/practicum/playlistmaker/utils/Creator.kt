package com.practicum.playlistmaker.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.player.data.impl.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.storage.SearchHistoryImpl
import com.practicum.playlistmaker.player.domain.api.intr.AudioPlayerInteractor
import com.practicum.playlistmaker.search.domain.api.intr.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.intr.SearchTracksInteractor
import com.practicum.playlistmaker.settings.domain.api.intr.SettingsInteractor
import com.practicum.playlistmaker.player.domain.api.repo.AudioPlayerRepository
import com.practicum.playlistmaker.search.domain.api.repo.TracksRepository
import com.practicum.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.search.data.network.TrackApi
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchTracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.impl.NavigatorImpl
import com.practicum.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.api.intr.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Creator(private val context: Context) { //не знаю, что делать с Creator, удалять не стал

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("search_history_shared_prefs", Context.MODE_PRIVATE)

    private val gson: Gson = Gson()
    private val searchHistory = SearchHistoryImpl(sharedPrefs = sharedPrefs, gson = gson)

    private val itunesService = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TrackApi::class.java)

    private val tracksRepository: TracksRepository by lazy { // для поиска треков
        TracksRepositoryImpl(RetrofitNetworkClient(context, itunesService = itunesService), searchHistory, context)
    }

    private val settingsRepository by lazy { // для настроек светлой/тёмной темы
        SettingsRepositoryImpl(context)
    }

    private val sharingRepository by lazy {
        SharingRepositoryImpl(context, NavigatorImpl(context))
    }

    private val mediaPlayerRepository: AudioPlayerRepository by lazy { // для управления аудиоплеером
        AudioPlayerRepositoryImpl()
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

    val mediaPlayerInteractor: AudioPlayerInteractor by lazy {
        AudioPlayerInteractorImpl(mediaPlayerRepository)
    }
}