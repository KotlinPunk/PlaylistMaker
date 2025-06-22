package com.practicum.playlistmaker.di

import com.google.gson.Gson
import com.practicum.playlistmaker.library.data.impl.LibraryDbRepositoryImpl
import com.practicum.playlistmaker.library.data.impl.PlaylistRepositoryImpl
import com.practicum.playlistmaker.library.domain.api.repo.LibraryDbRepository
import com.practicum.playlistmaker.library.domain.api.repo.PlaylistRepository
import com.practicum.playlistmaker.player.data.impl.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.repo.AudioPlayerRepository
import com.practicum.playlistmaker.search.data.PlaylistDbConvertor
import com.practicum.playlistmaker.search.data.TrackDbConvertor
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.repo.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.repo.SettingsRepository
import com.practicum.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.api.repo.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    //search
    factory<TracksRepository> {
        TracksRepositoryImpl(get(), get(), androidContext(), get())
    }

    //player
    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl()
    }

    //sharing
    factory<SharingRepository> {
        SharingRepositoryImpl(androidContext(), get())
    }

    //settings
    factory<SettingsRepository> {
        SettingsRepositoryImpl(context = androidContext())
    }

    //convector
    factory< TrackDbConvertor > { TrackDbConvertor() }
    factory< PlaylistDbConvertor > { PlaylistDbConvertor() }

    //library
    factory<LibraryDbRepository> {
        LibraryDbRepositoryImpl(get(), get())
    }
    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), Gson())
    }
}