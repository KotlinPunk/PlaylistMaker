package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.impl.storage.SearchHistoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.TrackApi
import com.practicum.playlistmaker.sharing.data.Navigator
import com.practicum.playlistmaker.sharing.data.impl.NavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.sin

val dataModule = module {

    //search
    single<TrackApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApi::class.java)
    }

    single {
        androidContext().getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    single { Gson() }

    single {
        SearchHistoryImpl(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

    //sharing
    single<Navigator> {
        NavigatorImpl(androidContext())
    }
}