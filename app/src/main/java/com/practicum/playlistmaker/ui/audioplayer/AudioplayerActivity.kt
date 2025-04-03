package com.practicum.playlistmaker.ui.audioplayer

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.domain.api.intr.MediaPlayerInteractor
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private var _binding: ActivityAudioplayerBinding? = null
    private val binding: ActivityAudioplayerBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private var track: Track? = null
    private val creator: Creator by lazy { Creator(this) }
    private val mediaPlayerInteractor: MediaPlayerInteractor by lazy {
        creator.mediaPlayerInteractor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_DATA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_DATA) as? Track
        }

        mediaPlayerInteractor.preparePlayerIntr(
            track?.previewUrl,
            onPrepared = {
                binding.playTrack.isEnabled = true
            },
            onCompletion = {
                binding.timePlayTrack.text = BY_ZEROS
                binding.playTrack.setImageResource(R.drawable.ic_play_track_button)
            },
            onTimeUpdate = { time ->
                binding.timePlayTrack.text = time
            }
        )

        setDataTrack()

        binding.playTrack.setOnClickListener {
            playbackControl()
        }

        binding.arrowback.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerInteractor.pausePlayerIntr()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayerInteractor.pausePlayerIntr()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerInteractor.releasePlayerIntr()
    }

    private fun playbackControl() {
        if (mediaPlayerInteractor.isPlayingPlayerIntr()) {
            mediaPlayerInteractor.pausePlayerIntr()
            binding.playTrack.setImageResource(R.drawable.ic_play_track_button)
        } else {
            mediaPlayerInteractor.startPlayerIntr()
            binding.playTrack.setImageResource(R.drawable.ic_pause_track_button)
        }
    }

    private fun setDataTrack() {
        with(binding) {
            nameTrackData.text = track?.trackName
            singerTrackData.text = track?.artistName
            timeTrackData.text = track?.getTimeTrack()
            albumTrackData.text = track?.collectionName
            yearTrackData.text = track?.getYearTrack()
            genreTrackData.text = track?.primaryGenreName
            countryTrackData.text = track?.country

            if (track?.collectionName.isNullOrEmpty()) {
                albumTrackData.visibility = View.GONE
                albumTrack.visibility = View.GONE
            } else {
                albumTrackData.text = track?.collectionName
            }

            Glide.with(this@AudioplayerActivity)
                .load(track?.getCoverArtwork())
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
        private const val BY_ZEROS = "0:00"
    }
}