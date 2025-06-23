package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.practicum.playlistmaker.R
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.domain.models.PlaylistFragmentState
import com.practicum.playlistmaker.library.viewmodel.PlaylistFragmentViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.String
import kotlin.collections.List

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding: FragmentPlaylistBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private val viewModel by viewModel<PlaylistFragmentViewModel>()
    private var adapter: PlaylistAdapter? = null
    private val playlists = ArrayList<Playlist>()
    private var job: Job? = null


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

        adapter = PlaylistAdapter(playlists)
        binding.playlistRV.adapter = adapter

        binding.newPlaylistButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment2)
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistFragmentState.Content -> showPlaylistTracks(state.playlistTracks)
                is PlaylistFragmentState.Error -> showErrorMessage(state.message)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch { // обновляем список плейлистов
            viewModel.updatePlaylistsFlow.collect {
                viewModel.fillData()
            }
        }


        adapter?.onClickPL = { playlist: Playlist ->
                clickDebounce()
                val playlistId = playlist.playlistId ?: -1L // если playlistId null, присваиваем значение по умолчанию (-1L)
                if (playlistId != -1L) {
                    val bundle = Bundle().apply {
                        putLong(PLAYLIST_ID, playlistId)
                    }
                    findNavController().navigate(
                        R.id.action_mediaLibraryFragment_to_infoOfPlaylistsFragment,
                        bundle
                    )
                } else {
                    Log.e("PlaylistAdapter", "playlistId is null for playlist: ${playlist.playlistName}")
                }
        }
    }

    private fun showPlaylistTracks(favTracks: List<Playlist>) {
        Log.d("PlaylistFragment", "showPlaylistTracks called with ${favTracks.size} tracks")
        binding.newPlaylistButton.isVisible = true
        binding.placeholderErrorImageFragment.isVisible = false
        binding.placeholderErrorTextFragment.isVisible = false
        binding.playlistRV.isVisible = true
        binding.playlistScroll.isVisible = true

        playlists.clear()
        playlists.addAll(favTracks)
        adapter?.notifyDataSetChanged()
        Log.d("PlaylistFragment", "Adapter notified of data change")
    }

    private fun showErrorMessage(errorMessage: String) {
        binding.newPlaylistButton.isVisible = true
        binding.placeholderErrorImageFragment.isVisible = true
        binding.placeholderErrorTextFragment.isVisible = true
        binding.playlistRV.isVisible = false
        binding.placeholderErrorTextFragment.text = errorMessage
    }

    private fun clickDebounce() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val PLAYLIST_ID = "playlist_id"

    }
}


