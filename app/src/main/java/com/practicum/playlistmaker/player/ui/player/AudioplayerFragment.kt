/*
package com.practicum.playlistmaker.player.ui.player

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioplayerBinding
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.player.ui.player.AudioplayerActivity
import com.practicum.playlistmaker.player.ui.player.AudioplayerActivity.Companion.TRACK_DATA
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import com.practicum.playlistmaker.search.data.models.TrackData
import com.practicum.playlistmaker.search.domain.models.Track

class AudioplayerFragment : Fragment() {

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding: FragmentAudioplayerBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private val viewModel by viewModel<AudioPlayerViewModel>()
    private var trackData: TrackData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        */
/*trackData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_DATA, TrackData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<TrackData>(TRACK_DATA)
        }*//*


        val track: Track? = trackData?.toTrack()

        track?.let { viewModel.setTrack(it) }

        viewModel.track.observe(viewLifecycleOwner) { track ->
            setDataTrack(track)
        }

        viewModel.isPrepared.observe(viewLifecycleOwner) { isPrepared ->
            binding.playTrack.isEnabled = isPrepared
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.playTrack.setImageResource(
                if (isPlaying) R.drawable.ic_pause_track_button
                else R.drawable.ic_play_track_button
            )
        }

        viewModel.currentTime.observe(viewLifecycleOwner) { time ->
            binding.timePlayTrack.text = time
        }

        binding.playTrack.setOnClickListener {
            viewModel.togglePlaybackControl()
        }

        binding.arrowback.setOnClickListener {
            findNavController().navigateUp()
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
        _binding=null
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

            Glide.with(requireContext())
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder_312_x_312)
                .transform(
                    RoundedCorners(
                        resources.getDimensionPixelOffset(
                            R.dimen.eight_dp
                        )
                    )
                )
                .into(placeholderTrack)
        }


    }
}*/
