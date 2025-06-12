package com.practicum.playlistmaker.library.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistFragmentViewModel : ViewModel() {

    private val stateLiveData =
        MutableLiveData<PlaylistFragmentState>(PlaylistFragmentState.Error("Нет плейлистов"))

    fun observeState(): LiveData<PlaylistFragmentState> = stateLiveData
}