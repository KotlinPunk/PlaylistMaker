package com.practicum.playlistmaker.search.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson


@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val playlistName: String?,
    val playlistDescription: String,
    val playlistCoverPath: String?,
    val trackIdsJson: String = "[]", // Для хранения списка ID треков, используем Gson
    val trackCount: Int = 0
)

// Функция для преобразования списка Track ID в JSON строку
fun List<Int>.toJson(): String = Gson().toJson(this)

// Функция для преобразования JSON строки в список Track ID
fun String.fromJson(): List<Int> = Gson().fromJson(this, List::class.java) as List<Int>