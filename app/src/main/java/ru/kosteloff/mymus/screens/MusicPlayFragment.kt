package ru.kosteloff.mymus.screens

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ru.kosteloff.mymus.R
import ru.kosteloff.mymus.databinding.FragmentMusicPlayBinding
import ru.kosteloff.mymus.helper.Constant
import ru.kosteloff.mymus.model.SongModel

class MusicPlayFragment : Fragment() {

    private var _binding: FragmentMusicPlayBinding? = null
    private val binding get() = _binding!!
    private val safeArgs: MusicPlayFragmentArgs by navArgs()
    lateinit var songModel: SongModel
    private var mediaPlayer: MediaPlayer? = null
    private var seekLength: Int = 0
    private val seekForwardTime = 5 * 1000
    private val seekBackwardTime = 5 * 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMusicPlayBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songModel = safeArgs.song!!
        mediaPlayer = MediaPlayer()

        binding.tvTitle.text = songModel.songTitle
        binding.tvDuration.text = songModel.songDuration
        binding.tvAuthor.text = songModel.songArtist

        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(songModel.songUri)
        val data = mmr.embeddedPicture

        if (data != null) {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            binding.ibCover.setImageBitmap(bitmap)
        }

        binding.ibPlay.setOnClickListener {
            playSong()
        }

        binding.ibRepeat.setOnClickListener {

            if (!mediaPlayer!!.isLooping) {
                mediaPlayer!!.isLooping = true
                binding.ibRepeat.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity?.applicationContext!!,
                        R.drawable.ic_repeat_white
                    )
                )
            } else {
                mediaPlayer!!.isLooping = false
                binding.ibRepeat.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity?.applicationContext!!,
                        R.drawable.ic_repeat
                    )
                )
            }
        }

        binding.ibForwardSong.setOnClickListener {
            forwardSong()
        }

        binding.ibBackwardSong.setOnClickListener {
            rewindSong()
        }
    }

    private fun forwardSong() {
        if (mediaPlayer != null) {
            val currentPosition: Int = mediaPlayer!!.currentPosition
            if (currentPosition + seekForwardTime <= mediaPlayer!!.duration) {
                mediaPlayer!!.seekTo(currentPosition + seekForwardTime)
            } else {
                mediaPlayer!!.seekTo(mediaPlayer!!.duration)
            }
        }
    }

    private fun rewindSong() {
        if (mediaPlayer != null) {
            val currentPosition: Int = mediaPlayer!!.currentPosition
            if (currentPosition - seekBackwardTime >= 0) {
                mediaPlayer!!.seekTo(currentPosition - seekBackwardTime)
            } else {
                mediaPlayer!!.seekTo(0)
            }
        }
    }

    private fun playSong() {

        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(songModel.songUri)
            mediaPlayer!!.prepare()
            mediaPlayer!!.seekTo(seekLength)
            mediaPlayer!!.start()

            binding.ibPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!,
                    R.drawable.ic_pause
                )
            )
            updateSeekBar()
        } else {

            mediaPlayer!!.pause()
            seekLength = mediaPlayer!!.currentPosition
            binding.ibPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!,
                    R.drawable.ic_play
                )
            )
        }
    }

    private fun updateSeekBar() {
        if (mediaPlayer != null) {
            binding.tvCurrentTime.text =
                Constant.durationConverter(mediaPlayer!!.currentPosition.toLong())
        }
        seekBarSetUp()
        Handler().postDelayed(runnable, 50)
    }

    var runnable = Runnable { updateSeekBar() }

    private fun seekBarSetUp() {

        if (mediaPlayer != null) {
            binding.seekBar.progress = mediaPlayer!!.currentPosition
            binding.seekBar.max = mediaPlayer!!.duration
        }
        binding.seekBar.setOnSeekBarChangeListener(@SuppressLint("AppCompatCustomView")
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                    binding.tvCurrentTime.text = Constant.durationConverter(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    if (seekBar != null) {
                        mediaPlayer!!.seekTo(seekBar.progress)
                    }
                }
            }
        })
    }

    private fun clearMediaPlayer(){
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
            }
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    override fun onStop() {
        super.onStop()
        playSong()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }
}