package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.library.domain.models.PlaylistFragmentState
import com.practicum.playlistmaker.library.viewmodel.PlaylistFragmentViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.String
import kotlin.collections.List

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding: FragmentPlaylistBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private val viewModel by viewModel<PlaylistFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistFragmentState.Content -> showPlaylistTracks(state.playlistTracks)
                is PlaylistFragmentState.Error -> showErrorMessage(state.message)
            }
        }
    }

    private fun showPlaylistTracks(favTracks: List<List<Track>>) {
        TODO("Not yet implemented")
    }

    private fun showErrorMessage(errorMessage: String) {
        binding.newPlaylist.isVisible = true
        binding.placeholderErrorImageFragment.isVisible = true
        binding.placeholderErrorTextFragment.isVisible = true
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}