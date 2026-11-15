package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.sharing.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(link: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }

    override fun openLink(link: String) {
        val openIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(link)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(openIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val emailIntent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, emailData.to)
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(emailIntent)
    }
}
