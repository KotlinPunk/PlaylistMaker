package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.library.domain.models.FavoriteFragmentState
import com.practicum.playlistmaker.library.viewmodel.FavoriteFragmentViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.String
import kotlin.collections.List

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private val viewModel by viewModel<FavoriteFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteFragmentState.Content -> showFavoriteTracks(state.favoriteTracks)
                is FavoriteFragmentState.Error -> showErrorMessage(state.message)
            }
        }
    }

    private fun showFavoriteTracks(favTracks: List<Track>) {
        TODO("Not yet implemented")
    }

    private fun showErrorMessage(errorMessage: String) {
        binding.placeholderErrorImageFragment.isVisible = true
        binding.placeholderErrorTextFragment.isVisible = true
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }
}


