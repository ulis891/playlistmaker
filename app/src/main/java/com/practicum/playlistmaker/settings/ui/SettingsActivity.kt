package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.sharing.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.EmailData
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl

class SettingsActivity : AppCompatActivity() {


    private var settingsViewModel: SettingsViewModel? = null
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharingInteractor: SharingInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharingInteractor = SharingInteractorImpl(object : ExternalNavigator {
            override fun shareLink(link: String): Intent {
                return Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, link)
                }
            }

            override fun openLink(link: String): Intent {
                return Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(link)
                }
            }

            override fun openEmail(emailData: EmailData): Intent {
                return Intent().apply {
                    action = Intent.ACTION_SENDTO
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, emailData.to)
                    putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
                    putExtra(Intent.EXTRA_TEXT, emailData.body)
                }
            }
        })


        settingsViewModel = ViewModelProvider(this, SettingsViewModel.getFactory()).get(
            SettingsViewModel::class.java)
        settingsViewModel?.observeDarkMode()?.observe(this){ isDark ->
            binding.switchDarkMode.isChecked = isDark
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel?.toggleDarkMode(isChecked)
            (application as App).switchTheme(isChecked)
        }

        binding.backArrow.setOnClickListener { finish() }

        binding.clickableShareRow.setOnClickListener {
            val intent = sharingInteractor.shareApp(getString(R.string.url_practicum))
            startActivity(Intent.createChooser(intent, getString(R.string.share_apk_title)))
        }


        binding.clickableSupportRow.setOnClickListener {
            val emailData = EmailData(
                to = arrayOf(getString(R.string.my_email)),
                subject = getString(R.string.mail_subject),
                body = getString(R.string.mail_text)
            )
            startActivity(sharingInteractor.openSupport(emailData))
        }

        binding.clickableAgreementRow.setOnClickListener {
            startActivity(sharingInteractor.openTerms(getString(R.string.url_agreement)))
        }
    }
}
