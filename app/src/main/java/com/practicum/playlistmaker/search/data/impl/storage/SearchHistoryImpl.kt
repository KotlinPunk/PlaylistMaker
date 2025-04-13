package com.practicum.playlistmaker.search.data.impl.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.dto.TrackDto

class SearchHistoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) {

    private val trackListHistory = getHistoryTrackList().toMutableList()

    fun addTrackInHistoryTrackList(track: TrackDto) {
        val trackIdentificator = trackListHistory.indexOfFirst { it.trackId == track.trackId }
        if (trackIdentificator != -1) {
            trackListHistory.removeAt(trackIdentificator)
            trackListHistory.add(0, track)
        } else {
            trackListHistory.add(0, track)
        }
        if (trackListHistory.size > SEARCH_HISTORY_SIZE) {
            trackListHistory.removeLast()
        }
        saveTrackInHistoryTrackList(trackListHistory)
    }

    fun saveTrackInHistoryTrackList(trackList: List<TrackDto>) {
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, createJsonFromTrackList(trackList))
            .apply()
    }

    fun getHistoryTrackList(): List<TrackDto> {
        val track = sharedPrefs.getString(SEARCH_HISTORY_KEY, null)
        return track?.let { createTrackListFromJson(track) } ?: emptyList()
    }

    fun clearHistoryTrackList() {
        trackListHistory.clear()
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    private fun createJsonFromTrackList(trackList: List<TrackDto>): String { //передаём
        return gson.toJson(trackList)
    }

    private fun createTrackListFromJson(json: String): List<TrackDto> { //получаем
        val type = object : TypeToken<List<TrackDto>>() {}.type
        return gson.fromJson(json, type)
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history_key"
        private const val SEARCH_HISTORY_SIZE = 10
    }
}