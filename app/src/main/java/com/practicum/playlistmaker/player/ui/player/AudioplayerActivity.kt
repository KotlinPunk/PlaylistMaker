package com.practicum.playlistmaker.player.ui.player

import com.practicum.playlistmaker.search.data.models.TrackData
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.domain.models.PlaylistFragmentState
import com.practicum.playlistmaker.library.ui.NewPlaylistFragment
import com.practicum.playlistmaker.library.ui.PlaylistAdapterMini
import com.practicum.playlistmaker.player.domain.models.AudioplayerState
import com.practicum.playlistmaker.player.domain.models.PlaylistStateInPlayer
import com.practicum.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioplayerActivity : AppCompatActivity() {

    private var _binding: ActivityAudioplayerBinding? = null
    private val binding: ActivityAudioplayerBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private var trackData: TrackData? = null
    private val viewModel by viewModel<AudioPlayerViewModel>()
    private var adapterPlayer: PlaylistAdapterMini? = null
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterPlayer = PlaylistAdapterMini(emptyList())
        binding.bottomRV.adapter = adapterPlayer



        viewModel.stateLiveDataPL.observe(this) { state ->
            when (state) {
                is PlaylistFragmentState.Content -> showPlaylistTracks(state.playlistTracks)
                is PlaylistFragmentState.Error -> showErrorMessage(state.message)
            }
        }

        viewModel.statePLInPlayer.observe(this) { state ->
            when (state) {
                is PlaylistStateInPlayer.AddedToPlaylist -> Toast.makeText(
                    applicationContext,
                    getString(R.string.added_to_playlist, state.namePL),
                    Toast.LENGTH_LONG
                ).show()

                is PlaylistStateInPlayer.PresentInPlaylist -> Toast.makeText(
                    applicationContext,
                    getString(R.string.present_in_playlist, state.namePL),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.addTrack.setOnClickListener {
            if (clickDebounce()) {
                viewModel.fillDataPL()
                setBottomSheetBehavior(isVisible = true)
            }
        }

        binding.createPlaylistButton.setOnClickListener {
            if (clickDebounce()) {
                val fragment = NewPlaylistFragment()
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().add(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null) // позволяет при свайпе или нажатии кнопки "назад" вернуться в активити
                    .commit()
                binding.fragmentContainerView.isVisible = true
                setBottomSheetBehavior(isVisible = false)

            }
        }

        trackData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_DATA, TrackData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<TrackData>(TRACK_DATA)
        }

        val track: Track? = trackData?.toTrack()
        track?.let {
            viewModel.setTrack(it)
            with(binding) {
                nameTrackData.text = track.trackName
                singerTrackData.text = track.artistName
                timeTrackData.text = track.getTimeTrack()
                albumTrackData.text = track.collectionName
                yearTrackData.text = track.getYearTrack()
                genreTrackData.text = track.primaryGenreName
                countryTrackData.text = track.country

                if (track.collectionName.isNullOrEmpty()) {
                    albumTrackData.visibility = View.GONE
                    albumTrack.visibility = View.GONE
                } else {
                    albumTrackData.text = track.collectionName
                }

                Glide.with(applicationContext)
                    .load(track.getCoverArtwork())
                    .placeholder(R.drawable.ic_placeholder_312_x_312)
                    .transform(
                        RoundedCorners(
                            this@AudioplayerActivity.resources.getDimensionPixelOffset(
                                R.dimen.eight_dp
                            )
                        )
                    )
                    .into(placeholderTrack)
            }
        }

        adapterPlayer?.onClickPl = { playlists: Playlist ->
            if (clickDebounce()) {
                viewModel.addTrackToPlaylist(track = track!!, playlists)
            }
        }

        viewModel.playerState.observe(this) { state ->
            updatePlayerState(state)
        }

        viewModel.currentTime.observe(this) { time ->
            binding.timePlayTrack.text = time
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            updateButtonFavorite(isFavorite)
        }

        binding.favoriteTrack.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.playTrack.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.arrowback.setOnClickListener {
            finish()
        }
    }

    private fun updateButtonFavorite(isFavorite: Boolean) {
        binding.favoriteTrack.setImageResource(
            if (isFavorite) {
                R.drawable.ic_favorite_track_button_on
            } else {
                R.drawable.ic_favorite_track_button
            }
        )
    }

    private fun updatePlayerState(state: AudioplayerState) {
        binding.playTrack.setImageResource(
            when (state) {
                AudioplayerState.State_default -> R.drawable.ic_play_track_button
                AudioplayerState.State_prepared -> R.drawable.ic_play_track_button
                AudioplayerState.State_playing -> R.drawable.ic_pause_track_button
                AudioplayerState.State_paused -> R.drawable.ic_play_track_button
                AudioplayerState.State_completed -> R.drawable.ic_play_track_button
            }
        )
    }

    private fun showPlaylistTracks(favPlaylists: List<Playlist>) {
        binding.playlistScroll.isVisible = true
        binding.bottomRV.isVisible = true
        if (adapterPlayer == null) { //для обновления адептера и для реакции слушателя
            adapterPlayer = PlaylistAdapterMini(favPlaylists)
            binding.bottomRV.adapter = adapterPlayer
        } else {
            adapterPlayer?.updateData(favPlaylists) // Создайте метод updateData в адаптере
        }
    }

    private fun showErrorMessage(errorMessage: String) {
        binding.playlistScroll.isVisible = false
        binding.bottomRV.isVisible = false
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun setBottomSheetBehavior(isVisible: Boolean){
        val bottomSheetContainer = binding.bottomSheet
        val overlay = binding.overlay
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        if (isVisible){
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

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayerVM()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TRACK_DATA = "track_data"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
