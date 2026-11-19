package com.practicum.playlistmaker.sharing.domain

interface SharingInteractor {
    fun shareApp(link: String)
    fun openTerms(link: String)
    fun openSupport(emailData: EmailData)
}
