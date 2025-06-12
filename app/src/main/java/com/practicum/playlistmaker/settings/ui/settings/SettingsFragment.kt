package com.practicum.playlistmaker.settings.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.share?.setOnClickListener {
            viewModel.shareApp()
        }

        binding.support?.setOnClickListener {
            viewModel.openSupport()
        }

        binding.agreement?.setOnClickListener {
            viewModel.openTerms()
        }
        viewModel.isDarkThemeEnabled.observe(viewLifecycleOwner) { isDarkThemeEnabled ->
            binding.themeSwitcher?.isChecked = isDarkThemeEnabled
        }

        binding.themeSwitcher?.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}