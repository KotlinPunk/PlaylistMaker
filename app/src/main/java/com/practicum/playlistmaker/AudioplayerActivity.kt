package com.practicum.playlistmaker

import android.os.Bundle
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

class AudioplayerActivity : AppCompatActivity() {


    private var _binding: ActivityAudioplayerBinding? = null
    private val binding: ActivityAudioplayerBinding  get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = Gson().fromJson(intent.getStringExtra(TRACK_DATA), Track::class.java)

        setDataTrack()

        binding.timePlayTrack.text = TIME_PLAY_TRACK

        binding.arrowback.setOnClickListener {
            finish()
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
        private const val TIME_PLAY_TRACK = "0:30"
    }
}