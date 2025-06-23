package com.practicum.playlistmaker.library.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentInfoOfPlaylistsBinding
import com.practicum.playlistmaker.library.domain.models.FavoriteFragmentState
import com.practicum.playlistmaker.library.domain.models.InfoOfPlaylistState
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.domain.models.PlaylistFragmentState
import com.practicum.playlistmaker.library.viewmodel.InfoOfPlaylistsViewModel
import com.practicum.playlistmaker.library.viewmodel.PlaylistFragmentViewModel
import com.practicum.playlistmaker.player.ui.player.AudioplayerActivity
import com.practicum.playlistmaker.search.data.models.TrackData
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.search.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit
import kotlin.getValue

class InfoOfPlaylistsFragment : Fragment() {

    private var _binding: FragmentInfoOfPlaylistsBinding? = null
    private val binding: FragmentInfoOfPlaylistsBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private var adapter: TrackAdapter? = null
    private val trackListFavorite = ArrayList<Track>()

    private val viewModel by viewModel<InfoOfPlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoOfPlaylistsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getLong(PLAYLIST_ID)
        viewModel.loadPlaylistData(playlistId)
        viewModel.fillTrackData(playlistId)

        adapter = TrackAdapter(trackListFavorite)
        binding.bottomRvPl.adapter = adapter

        adapter?.onClickTrack = { track ->
            navigateToPlayer(track)
        }

        adapter?.onLongClickTrack ={ track ->
            AlertDialog.Builder(requireContext()/*, R.style.AlertDialogTheme*/)
                .setMessage("Хотите удалить трек?")
                .setNegativeButton("НЕТ") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("ДА") { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deleteTrackToPlaylist(track,playlistId)
                }
                .show()
        }



        viewModel.stateLiveTrackData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteFragmentState.Content -> showFavoriteTracks(favTracks = state.favoriteTracks)
                is FavoriteFragmentState.Error -> showErrorMessage(errorMessage = state.message)
            }
        }





        viewModel.playlistInfo.observe(viewLifecycleOwner) { state ->
            when(state){
                is InfoOfPlaylistState.Content -> showContent(state.playlist)

                is InfoOfPlaylistState.Error -> TODO()
            }
        }

        binding.arrowbackPL.setOnClickListener {
            findNavController().navigateUp()
        }

    }


    private fun showContent(playlist: Playlist?){
        playlist?.let{
            with(binding) {
                namePL.text = playlist.playlistName
                descriptionPL.text = playlist.playlistDescription
                countPL.text = "${playlist.trackCount} ${endingCount(playlist.trackCount)}"
                it.totalDuration?.let { totalDuration ->
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(totalDuration)
                    durationPL.text = String.format("%d", minutes) + " ${endingDuration(minutes)}"
                } ?: run {
                    durationPL.text = "Заглушка"
                }
                if (playlist.playlistCoverPath != null) {
                    Glide.with(requireContext())
                        .load(playlist.playlistCoverPath)
                        .transform(CenterCrop())
                        .placeholder(R.drawable.ic_placeholder_312_x_312)
                        .into(binding.imagePL)

                }
            }
        }
    }


    private fun showFavoriteTracks(favTracks: List<Track>) {
        binding.bottomRvPl.isVisible = true
        binding.trackScrollPL.isVisible = true


        trackListFavorite.clear()
        trackListFavorite.addAll(favTracks)
        adapter?.notifyDataSetChanged()
    }

    private fun showErrorMessage(errorMessage: String) {
        binding.bottomRvPl.isVisible = false
        binding.trackScrollPL.isVisible = false

    }




    private fun navigateToPlayer(track: Track) {

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
            action.putExtra(InfoOfPlaylistsFragment.Companion.TRACK_DATA, trackData)
            startActivity(action)

    }

    private fun endingCount(trackCount: Int?): String {
        if (trackCount == null) return "треков"
        return when {
            trackCount % 100 in 11..14 -> "треков"
            trackCount % 10 == 1 -> "трек"
            trackCount % 10 in 2..4 -> "трека"
            else -> "треков"
        }
    }

    private fun endingDuration(duration: Long?): String {
        if (duration == null) return "минут"
        return when {
            duration % 100 in 11..14 -> "минут"
            (duration % 10).toInt() == 1 -> "минута"
            duration % 10 in 2..4 -> "минуты"
            else -> "минут"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        private const val PLAYLIST_ID = "playlist_id"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK_DATA = "track_data"
    }

}