package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.domain.api.intr.LibraryDbInteractor
import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.impl.LibraryDbInteractorImpl
import com.practicum.playlistmaker.library.domain.impl.PlaylistInteractorImpl
import com.practicum.playlistmaker.player.domain.api.intr.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.api.intr.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.intr.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchTracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.api.intr.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.api.intr.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    //search
    single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
    single<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }

    //player
    single<AudioPlayerInteractor> { AudioPlayerInteractorImpl(get()) }

    //sharing
    single<SharingInteractor> { SharingInteractorImpl(get()) }

    //settings
    single<SettingsInteractor> { SettingsInteractorImpl(get()) }

    //library
    single<LibraryDbInteractor> { LibraryDbInteractorImpl(get()) }
    single<PlaylistInteractor> { PlaylistInteractorImpl(get()) }
}