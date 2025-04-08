package com.practicum.playlistmaker.settings.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.utils.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.domain.api.intr.SettingsInteractor
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private var _binding: ActivitySettingsBinding? = null
    private val binding: ActivitySettingsBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowback.setOnClickListener {
            finish()
        }

        binding.share?.setOnClickListener {
            viewModel.shareApp()
        }

        binding.support?.setOnClickListener {
            viewModel.openSupport()
        }

        binding.agreement?.setOnClickListener {
            viewModel.openTerms()
        }
        viewModel.isDarkThemeEnabled.observe(this) { isDarkThemeEnabled ->
            binding.themeSwitcher?.isChecked = isDarkThemeEnabled
        }

        binding.themeSwitcher?.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchTheme(checked)
        }
    }
}