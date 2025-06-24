package com.practicum.playlistmaker.library.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentInfoOfPlaylistsBinding
import com.practicum.playlistmaker.databinding.PlaylistMiniItemBinding
import com.practicum.playlistmaker.library.domain.models.FavoriteFragmentState
import com.practicum.playlistmaker.library.domain.models.InfoOfPlaylistState
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.viewmodel.InfoOfPlaylistsViewModel
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
    private lateinit var itemBinding: PlaylistMiniItemBinding
    private lateinit var playlistElement: Playlist

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

        itemBinding =
            PlaylistMiniItemBinding.bind(binding.root.findViewById(R.id.small_playlist_card))
        adapter = TrackAdapter(trackListFavorite)
        binding.bottomRvPl.adapter = adapter

        adapter?.onClickTrack = { track ->
            navigateToPlayer(track)
        }

        /* binding.tvShare.setOnClickListener {
             shareApp()
         }
 */
        binding.tvEdit.setOnClickListener {

        }
        binding.deletePlaylist.setOnClickListener {
            dialogDeletePlaylist(playlistElement)
        }

        adapter?.onLongClickTrack = { track ->
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Хотите удалить трек?")
                .setNegativeButton("НЕТ") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("ДА") { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deleteTrackToPlaylist(track, playlistId)
                }
                .show()
        }
        /*binding.sharePL.setOnClickListener {
            shareApp()
        }*/

        binding.morePL.setOnClickListener {
            setBottomSheetBehavior(isVisible = true)
        }

        viewModel.stateLiveTrackData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteFragmentState.Content -> showFavoriteTracks(favTracks = state.favoriteTracks)
                is FavoriteFragmentState.Error -> showErrorMessage(errorMessage = state.message)
            }
        }

        viewModel.playlistInfo.observe(viewLifecycleOwner) { state ->
            when (state) {
                is InfoOfPlaylistState.Content -> showContent(state.playlist)

                is InfoOfPlaylistState.Error -> TODO()
            }
        }

        binding.arrowbackPL.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    /*    private fun shareApp() {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                val shareMessage = viewModel.getShareMessage(binding.countPL.text.toString())
                if (shareMessage.isBlank()) {
                    AlertDialog.Builder(requireContext())
                        .setMessage("В этом плейлисте нет списка треков, которым можно поделиться")
                        .setPositiveButton("OK", null)
                        .show()
                    return
                }
                putExtra(Intent.EXTRA_TEXT, shareMessage)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)))
        }*/

    private fun setBottomSheetBehavior(isVisible: Boolean) {
        val bottomSheetContainer = binding.BottomSheetUp
        val overlay = binding.overlay
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        if (isVisible) {
            bottomSheetContainer.isVisible = true
            overlay.isVisible = true // отобразить сразу, а не после обработки действия с BS
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            overlay.isVisible = false
                        }

                        else -> {}
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        } else {
            bottomSheetContainer.isVisible = false
            overlay.isVisible = false // скрываем оверлей
        }
    }

    private fun showContent(playlist: Playlist?) {
        playlist?.let {
            with(binding) {
                namePL.text = playlist.playlistName
                descriptionPL.text = playlist.playlistDescription
                countPL.text = "${playlist.trackCount} ${endingCount(playlist.trackCount)}"
                it.totalDuration?.let { totalDuration ->
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(totalDuration)
                    durationPL.text = String.format("%d", minutes) + " ${endingDuration(minutes)}"
                } ?: run {
                    durationPL.text = "Error"
                }
                if (playlist.playlistCoverPath != null) {
                    Glide.with(requireContext())
                        .load(playlist.playlistCoverPath)
                        .transform(CenterCrop())
                        .placeholder(R.drawable.ic_placeholder_312_x_312)
                        .into(binding.imagePL)
                    Glide.with(requireContext())
                        .load(playlist.playlistCoverPath)
                        .transform(CenterCrop())
                        .placeholder(R.drawable.ic_placeholder)
                        .into(itemBinding.imagePLMini)
                    itemBinding.namePLMini.text = playlist.playlistName
                    itemBinding.countPLMini.text =
                        "${playlist.trackCount} ${endingCount(playlist.trackCount)}"
                }
            }
        }
    }


    private fun dialogDeletePlaylist(playlist: Playlist) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить плейлист \"${playlist.playlistName}\"?")
            .setNegativeButton(requireActivity().getString(R.string.nope)) { _, _ ->

            }.setPositiveButton(requireActivity().getString(R.string.yep)) { _, _ ->
                viewModel.deletePlaylist(playlist.playlistId)
                findNavController().navigateUp()
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

    companion object {
        private const val PLAYLIST_ID = "playlist_id"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK_DATA = "track_data"
    }

}