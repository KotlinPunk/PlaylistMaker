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
    factory<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
    factory<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }

    //player
    factory<AudioPlayerInteractor> { AudioPlayerInteractorImpl(get()) }

    //sharing
    factory<SharingInteractor> { SharingInteractorImpl(get()) }

    //settings
    factory<SettingsInteractor> { SettingsInteractorImpl(get()) }

    //library
    factory<LibraryDbInteractor> { LibraryDbInteractorImpl(get()) }
    factory<PlaylistInteractor> { PlaylistInteractorImpl(get()) }
}