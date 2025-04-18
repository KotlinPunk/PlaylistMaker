package com.practicum.playlistmaker.player.ui.player

import com.practicum.playlistmaker.search.data.models.TrackData
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.player.domain.models.AudioplayerState
import com.practicum.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioplayerActivity : AppCompatActivity() {

    private var _binding: ActivityAudioplayerBinding? = null
    private val binding: ActivityAudioplayerBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private var trackData: TrackData? = null
    private val viewModel by viewModel<AudioPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_DATA, TrackData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<TrackData>(TRACK_DATA)
        }

        val track: Track? = trackData?.toTrack()
        track?.let {
            viewModel.setPreviewUrl(it.previewUrl)
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
        viewModel.playerState.observe(this) { state ->
            updatePlayerState(state)
        }

        viewModel.currentTime.observe(this) { time ->
            binding.timePlayTrack.text = time
        }

        binding.playTrack.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.arrowback.setOnClickListener {
            finish()
        }
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
    }
}