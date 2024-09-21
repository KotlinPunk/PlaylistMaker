package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val trackListHistory = getHistoryTrackList().toMutableList()

    fun addTrackInHistoryTrackList(track: Track) {
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

    fun saveTrackInHistoryTrackList(trackList: List<Track>) {
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, createJsonFromTrackList(trackList))
            .apply()
    }

    fun getHistoryTrackList(): List<Track> {
        val track = sharedPrefs.getString(SEARCH_HISTORY_KEY, null)
        return track?.let { createTrackListFromJson(track) } ?: emptyList()
    }

    fun clearHistoryTrackList() {
        trackListHistory.clear()
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    private fun createJsonFromTrackList(trackList: List<Track>): String { //передаём
        return Gson().toJson(trackList)
    }

    private fun createTrackListFromJson(json: String): List<Track> { //получаем
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history_key"
        private const val SEARCH_HISTORY_SIZE = 10
    }
}