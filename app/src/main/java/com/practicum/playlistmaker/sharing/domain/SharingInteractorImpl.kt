package com.practicum.playlistmaker.sharing.domain

import android.content.Intent
import com.practicum.playlistmaker.sharing.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.EmailData


class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp(link: String): Intent {
        return externalNavigator.shareLink(link)
    }

    override fun openTerms(link: String): Intent {
        return externalNavigator.openLink(link)
    }

    override fun openSupport(emailData: EmailData): Intent {
        return externalNavigator.openEmail(emailData)
    }
}
