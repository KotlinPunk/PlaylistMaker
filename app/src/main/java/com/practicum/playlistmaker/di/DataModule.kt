package com.practicum.playlistmaker.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.impl.storage.SearchHistoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.TrackApi
import com.practicum.playlistmaker.sharing.data.Navigator
import com.practicum.playlistmaker.sharing.data.impl.NavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    //search
    single<TrackApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

    single { Gson() }

    single {
        androidContext().getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    single {
        SearchHistoryImpl(get(), get())
    }

    //sharing
    single<Navigator> {
        NavigatorImpl(androidContext())
    }

    //db
    single {
        try {
            Log.d("Koin", "Creating AppDatabase")
            Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
        } catch (e: Exception) {
            Log.e("Koin", "Error creating AppDatabase: ", e)
            throw e
        }
    }
}