package com.practicum.playlistmaker.player.ui.player

import com.practicum.playlistmaker.search.data.models.TrackData
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
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

        track?.let { viewModel.setTrack(it) }

        viewModel.track.observe(this) { track ->
            setDataTrack(track)
        }

        viewModel.isPrepared.observe(this) { isPrepared ->
            binding.playTrack.isEnabled = isPrepared
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            binding.playTrack.setImageResource(
                if (isPlaying) R.drawable.ic_pause_track_button
                else R.drawable.ic_play_track_button
            )
        }

        viewModel.currentTime.observe(this) { time ->
            binding.timePlayTrack.text = time
        }

        binding.playTrack.setOnClickListener {
            viewModel.togglePlaybackControl()
        }

        binding.arrowback.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayerVM()
    }

    override fun onStop() {
        super.onStop()
        viewModel.pausePlayerVM()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayerVM()
    }

    private fun setDataTrack(track: Track) {
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

    companion object {
        private const val TRACK_DATA = "track_data"
    }
}