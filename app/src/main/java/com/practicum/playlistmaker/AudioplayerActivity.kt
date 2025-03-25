package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.databinding.TrackItemBinding
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private var _binding: ActivityAudioplayerBinding? = null
    private val binding: ActivityAudioplayerBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private var track: Track? = null
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var updateTimeRunnable: Runnable? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

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

        preparePlayer()
        setDataTrack()

        binding.playTrack.setOnClickListener {
            playbackControl()
        }

        updateTimeRunnable = getUpdateTimeTrack()

        binding.arrowback.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onStop() {
        super.onStop()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        updateTimeRunnable?.let(mainThreadHandler::removeCallbacks)
        updateTimeRunnable = null
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track?.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.playTrack.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            binding.timePlayTrack.text = BY_ZEROS
            binding.playTrack.setImageResource(R.drawable.ic_play_track_button)
            updateTimeRunnable?.let(mainThreadHandler::removeCallbacks)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playTrack.setImageResource(R.drawable.ic_pause_track_button)
        playerState = STATE_PLAYING
        mainThreadHandler.post(updateTimeRunnable!!)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.playTrack.setImageResource(R.drawable.ic_play_track_button)
        playerState = STATE_PAUSED
        updateTimeRunnable?.let(mainThreadHandler::removeCallbacks)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PAUSED, STATE_PREPARED -> {
                startPlayer()
            }

            STATE_DEFAULT -> {
                //empty
            }
        }
    }

    private fun getUpdateTimeTrack(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    binding.timePlayTrack.text = dateFormat.format(mediaPlayer.currentPosition)
                    mainThreadHandler.postDelayed(this, UPDATE_TIMER)
                }
            }
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
        private const val UPDATE_TIMER = 500L

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}