package com.practicum.playlistmaker.sharing.data

data class EmailData(
    val to: Array<String>,
    val subject: String,
    val body: String
)
