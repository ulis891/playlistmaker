package com.practicum.playlistmaker.sharing.domain

import android.content.Intent
import com.practicum.playlistmaker.sharing.data.EmailData

interface SharingInteractor {
    fun shareApp(link: String): Intent
    fun openTerms(link: String): Intent
    fun openSupport(emailData: EmailData): Intent
}

