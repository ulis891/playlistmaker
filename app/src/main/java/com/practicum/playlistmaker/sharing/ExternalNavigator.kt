package com.practicum.playlistmaker.sharing

import com.practicum.playlistmaker.sharing.data.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)
}
