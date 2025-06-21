package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.viewmodel.FavoriteFragmentViewModel
import com.practicum.playlistmaker.library.viewmodel.NewPlaylistFragmentViewModel
import com.practicum.playlistmaker.library.viewmodel.PlaylistFragmentViewModel
import com.practicum.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.viewmodel.SearchTrackViewModel
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    //search
    viewModel {
        SearchTrackViewModel(androidApplication(), get(), get(), get())
    }

    //player
    viewModel {
        AudioPlayerViewModel(androidApplication(), get(), get(), get())
    }

    //settings
    viewModel {
        SettingsViewModel(androidApplication(), get(), get())
    }

    //favoritefragment
    viewModel {
        FavoriteFragmentViewModel(androidContext(), get())
    }

    //playlistfragment
    viewModel {
        PlaylistFragmentViewModel(androidContext(), get())
    }

    //newPlaylistFragment
    viewModel {
        NewPlaylistFragmentViewModel(get())
    }
}