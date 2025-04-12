package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.viewmodel.SearchTrackViewModel
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    //search
    viewModel {
        SearchTrackViewModel(androidApplication(), get(), get())
    }

    //player
    viewModel {
        AudioPlayerViewModel(androidApplication(), get())
    }

    //settings
    viewModel {
        SettingsViewModel(androidApplication(), get(), get())
    }
}