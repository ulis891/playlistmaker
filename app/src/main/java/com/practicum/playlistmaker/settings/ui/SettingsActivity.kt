package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.sharing.domain.EmailData
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {

    private val settingsViewModel: SettingsViewModel by viewModel<SettingsViewModel>()

    private lateinit var binding: ActivitySettingsBinding

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

        settingsViewModel.observeDarkMode().observe(this) { isDark ->
            binding.switchDarkMode.isChecked = isDark
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleDarkMode(isChecked)
        }

        binding.backArrow.setOnClickListener { finish() }

        binding.clickableShareRow.setOnClickListener {
            settingsViewModel.shareApp(getString(R.string.url_practicum))
        }

        binding.clickableSupportRow.setOnClickListener {
            val emailData = EmailData(
                to = arrayOf(getString(R.string.my_email)),
                subject = getString(R.string.mail_subject),
                body = getString(R.string.mail_text)
            )
            settingsViewModel.openSupport(emailData)
        }

        binding.clickableAgreementRow.setOnClickListener {
            settingsViewModel.openTerms(getString(R.string.url_agreement))
        }
    }
}
