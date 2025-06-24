package com.practicum.playlistmaker.library.ui.frags

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class InfoOfPlaylistsFragment : Fragment() {

    private var _binding: FragmentInfoOfPlaylistsBinding? = null
    private val binding: FragmentInfoOfPlaylistsBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private var adapter: TrackAdapter? = null
    private val trackListFavorite = ArrayList<Track>()
    private lateinit var itemBinding: PlaylistMiniItemBinding
    private lateinit var playlistElement: Playlist
    private var job: Job? = null

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
        viewModel.subscribeToPlaylistChanges()

         setBottomSheetBehavior(isVisible = false)

        itemBinding =
            PlaylistMiniItemBinding.bind(binding.root.findViewById(R.id.small_playlist_card))
        adapter = TrackAdapter(trackListFavorite)
        binding.bottomRvPl.adapter = adapter

        adapter?.onClickTrack = { track ->
            clickDebounce()
            navigateToPlayer(track)
        }

        binding.deletePlaylist.setOnClickListener {
            dialogDeletePlaylist(playlistElement)
        }

        adapter?.onLongClickTrack = { track ->
            clickDebounce()
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.do_you_want_delete))
                .setNegativeButton(getString(R.string.nope)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.yep)) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deleteTrackToPlaylist(track, playlistId)
                }
                .show()
        }

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

        binding.tvShare.setOnClickListener {
            shareApp()
        }

        binding.sharePL.setOnClickListener {
            shareApp()
        }
    }

    private fun shareApp() {
        if (trackListFavorite.isEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.empty_list))
                .setPositiveButton("OK", null)
                .show()
        } else {
            val shareMessage = buildShareMessage(playlistElement, trackListFavorite)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareMessage)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylistData()
    }


    private fun setBottomSheetBehavior(isVisible: Boolean) {
        val bottomSheetContainer = binding.bottomSheetUp
        val overlay = binding.overlay
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        if (isVisible) {
            bottomSheetContainer.isVisible = true
            binding.bottomSheetPL.isVisible = false
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
            binding.bottomSheetPL.isVisible = true
            overlay.isVisible = false // скрываем оверлей
        }
    }


    private fun showContent(playlist: Playlist?) {
        Log.d(
            "InfoOfPlaylistsFragment",
            "showContent called with playlist: ${playlist?.playlistName}"
        )
        playlist?.let {
            playlistElement = it // инициализируем плейлист тут
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

            // обновляем BS с новыми данными
            updateBottomSheetContent(playlist)

            binding.deletePlaylist.setOnClickListener {
                Log.d(
                    "InfoOfPlaylistsFragment",
                    "Delete playlist clicked for: ${playlist.playlistName}"
                )
                dialogDeletePlaylist(playlistElement)
            }
            binding.tvEdit.setOnClickListener {
                Log.d(
                    "InfoOfPlaylistsFragment",
                    "Edit playlist clicked for: ${playlist.playlistName}"
                )
                val bundle = Bundle().apply {
                    putLong(PLAYLIST_ID, playlistElement.playlistId ?: -1)
                    putString("playlist_name", playlistElement.playlistName)
                    putString("playlist_description", playlistElement.playlistDescription)
                    putString("playlist_cover_path", playlistElement.playlistCoverPath)
                    putBoolean("is_editing", true)
                }
                findNavController().navigate(
                    R.id.action_infoOfPlaylistsFragment_to_newPlaylistFragment,
                    bundle
                )
            }
            Log.d(
                "InfoOfPlaylistsFragment",
                "UI updated with playlist data: name=${playlist.playlistName}, description=${playlist.playlistDescription}"
            )
        }
    }

    private fun updateBottomSheetContent(playlist: Playlist) { // обновляем данные о треке в BS
        Log.d(
            "InfoOfPlaylistsFragment",
            "Updating bottom sheet content for playlist: ${playlist.playlistName}"
        )
        itemBinding.namePLMini.text = playlist.playlistName
        itemBinding.countPLMini.text = "${playlist.trackCount} ${endingCount(playlist.trackCount)}"
        if (playlist.playlistCoverPath != null) {
            Glide.with(requireContext())
                .load(playlist.playlistCoverPath)
                .transform(CenterCrop())
                .placeholder(R.drawable.ic_placeholder)
                .into(itemBinding.imagePLMini)
        }
    }

    private fun dialogDeletePlaylist(playlist: Playlist) {
        Log.d(
            "InfoOfPlaylistsFragment",
            "dialogDeletePlaylist called for: ${playlist.playlistName}"
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.do_you_want_delete_pl) +" \"${playlist.playlistName}\"?")
            .setNegativeButton(requireActivity().getString(R.string.nope)) { _, _ ->
                Log.d("InfoOfPlaylistsFragment", "Delete cancelled")
            }.setPositiveButton(requireActivity().getString(R.string.yep)) { _, _ ->
                Log.d(
                    "InfoOfPlaylistsFragment",
                    "Delete confirmed for playlist: ${playlist.playlistName}"
                )
                viewModel.deletePlaylist(playlist.playlistId)
                findNavController().navigateUp()
            }.show()
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
        action.putExtra(TRACK_DATA, trackData)
        startActivity(action)
    }

    private fun endingCount(trackCount: Int?): String {
        if (trackCount == null) return getString(R.string.track5)
        return when {
            trackCount % 100 in 11..14 -> getString(R.string.track5)
            trackCount % 10 == 1 -> getString(R.string.track1)
            trackCount % 10 in 2..4 -> getString(R.string.track2_4)
            else -> getString(R.string.track5)
        }
    }

    private fun endingDuration(duration: Long?): String {
        if (duration == null) return getString(R.string.min5)
        return when {
            duration % 100 in 11..14 -> getString(R.string.min5)
            (duration % 10).toInt() == 1 -> getString(R.string.min1)
            duration % 10 in 2..4 -> getString(R.string.min2_4)
            else -> getString(R.string.min5)
        }
    }

    // строим сообщения
    private fun buildShareMessage(playlist: Playlist, tracks: List<Track>): String {
        val builder = StringBuilder()
        builder.append(playlist.playlistName).append("\n")
        if (playlist.playlistDescription.isNotBlank()) {
            builder.append(playlist.playlistDescription).append("\n")
        }
        builder.append("${tracks.size} ${endingCount(tracks.size)}").append("\n")
        tracks.forEachIndexed { index, track ->
            val duration = track.trackTimeMillis?.let { formatTrackTime(it) } ?: ""
            builder.append("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
            builder.append("\n")
        }
        return builder.toString().trim()
    }

    // Функция для форматирования времени трека в мм:сс
    private fun formatTrackTime(millis: Long): String {
        val minutes = (millis / (1000 * 60)) % 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
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
        private const val PLAYLIST_ID = "playlist_id"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK_DATA = "track_data"
    }

}
