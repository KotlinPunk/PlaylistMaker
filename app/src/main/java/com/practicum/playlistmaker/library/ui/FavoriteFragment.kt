package com.practicum.playlistmaker.library.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.library.domain.models.FavoriteFragmentState
import com.practicum.playlistmaker.library.viewmodel.FavoriteFragmentViewModel
import com.practicum.playlistmaker.player.ui.player.AudioplayerActivity
import com.practicum.playlistmaker.search.data.models.TrackData
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.search.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.String
import kotlin.collections.List

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private val viewModel by viewModel<FavoriteFragmentViewModel>()
    private var adapter: TrackAdapter? = null
    private val trackListFavorite = ArrayList<Track>()
    private var isClickAllowed = true
    private lateinit var rvTrackListFavorite: RecyclerView
    private lateinit var placeholderErrorImageFragment: ImageView
    private lateinit var placeholderErrorTextFragment: TextView

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


        adapter = TrackAdapter(trackListFavorite)
        adapter?.onClickTrack = { track ->
            navigateToPlayer(track)
        }
        rvTrackListFavorite = binding.rvFavoriteTrackListXml
        placeholderErrorImageFragment = binding.placeholderErrorImageFragment
        placeholderErrorTextFragment = binding.placeholderErrorTextFragment

        rvTrackListFavorite.adapter = adapter

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteFragmentState.Content -> showFavoriteTracks(favTracks = state.favoriteTracks)
                is FavoriteFragmentState.Error -> showErrorMessage(errorMessage = state.message)
            }
        }
    }

    private fun showFavoriteTracks(favTracks: List<Track>) {
        rvTrackListFavorite.isVisible = true
        placeholderErrorImageFragment.isVisible = false
        placeholderErrorTextFragment.isVisible = false

        trackListFavorite.clear()
        trackListFavorite.addAll(favTracks)
        adapter?.notifyDataSetChanged()
    }

    private fun showErrorMessage(errorMessage: String) {
        rvTrackListFavorite.isVisible = false
        placeholderErrorImageFragment.isVisible = true
        placeholderErrorTextFragment.isVisible = true
        placeholderErrorTextFragment.text = errorMessage
    }

    private fun navigateToPlayer(track: Track) {
        if (clickDebounce()) {
            val trackData = TrackData(
                track.trackName,
                track.artistName,
                track.trackTimeMillis,
                track.artworkUrl100,
                track.trackId,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.previewUrl,
                track.isFavorite
            )
            val action = Intent(requireContext(), AudioplayerActivity::class.java)
            action.putExtra(FavoriteFragment.Companion.TRACK_DATA, trackData)
            startActivity(action)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    companion object {
        fun newInstance() = FavoriteFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK_DATA = "track_data"
    }
}