package com.practicum.playlistmaker.sharing.domain

data class EmailData(
    val to: Array<String>,
    val subject: String,
    val body: String
)
