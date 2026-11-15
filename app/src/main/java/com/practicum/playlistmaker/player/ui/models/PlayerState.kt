package com.practicum.playlistmaker.player.ui.models

sealed interface PlayerState {
    object Default : PlayerState
    object Prepared : PlayerState
    data class Playing(val progress: String) : PlayerState
    data class Paused(val progress: String) : PlayerState
}
