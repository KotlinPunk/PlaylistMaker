package com.practicum.playlistmaker.player.ui.player

sealed interface AudioplayerState {
    object State_default : AudioplayerState
    object State_prepared : AudioplayerState
    object State_playing : AudioplayerState
    object State_paused : AudioplayerState
    object State_completed : AudioplayerState
}