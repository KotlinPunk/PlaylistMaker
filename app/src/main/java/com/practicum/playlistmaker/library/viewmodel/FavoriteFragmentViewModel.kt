package com.practicum.playlistmaker.library.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.library.domain.models.FavoriteFragmentState

class FavoriteFragmentViewModel : ViewModel() {

    private val stateLiveData =
        MutableLiveData<FavoriteFragmentState>(FavoriteFragmentState.Error("Пусто"))

    fun observeState(): LiveData<FavoriteFragmentState> = stateLiveData

    init {
    }

}