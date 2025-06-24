/*
package com.practicum.playlistmaker.library.ui.frags


import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.viewmodel.EditPlaylistFragmentViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : NewPlaylistFragment() {

    override val viewModel: EditPlaylistFragmentViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getLong(PLAYLIST_ID) ?: -1L
        binding.toolbarNewPL.title = "Редактировать"
        binding.newPlaylistButton.text = "Сохранить"

        viewModel.loadPlaylistData(playlistId)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
        binding.newPlaylistButton.setOnClickListener { // если кнопка false, можем редактировать
            viewModel.stateLiveData.value?.let { button ->
                if (button.isSaveButtinEnabled) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.savePlaylist()
                        findNavController().popBackStack()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Заполните название плейлиста",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            // обновили ui в зависимости от состояния
            if (!TextUtils.equals(binding.inputNameNewPL.text, state.namePL)) {
                binding.inputNameNewPL.setText(state.namePL)
            }
            if (!TextUtils.equals(binding.inputDescriptionNewPL.text, state.descriptionPL)) {
                binding.inputDescriptionNewPL.setText(state.descriptionPL)
            }
            // меняем состояние кнопки "Создать"
            binding.newPlaylistButton.apply {
                isEnabled = state.isSaveButtinEnabled
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        if (state.isSaveButtinEnabled) R.color.blue else R.color.gray
                    )
                )
            }
            if (state.coverPathPL.isNotEmpty()) {
                Glide.with(requireContext())
                    .load(state.coverPathPL.toUri())
                    .placeholder(R.drawable.ic_playlist)
                    .into(binding.imageNewPL)
            }
        }
    }


    companion object {
        private const val PLAYLIST_ID = "playlist_id"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


}*/
