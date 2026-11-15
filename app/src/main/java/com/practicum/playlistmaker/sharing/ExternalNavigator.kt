package com.practicum.playlistmaker.sharing

import android.content.Intent
import com.practicum.playlistmaker.sharing.data.EmailData


interface ExternalNavigator {
    fun shareLink(link: String): Intent
    fun openLink(link: String): Intent
    fun openEmail(emailData: EmailData): Intent
}
