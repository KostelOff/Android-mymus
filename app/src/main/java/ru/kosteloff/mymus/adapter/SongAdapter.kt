package ru.kosteloff.mymus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.kosteloff.mymus.databinding.SongLayoutBinding
import ru.kosteloff.mymus.model.SongModel
import ru.kosteloff.mymus.screens.ListMusicFragmentDirections

class SongAdapter : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(val binding: SongLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<SongModel>() {
        override fun areItemsTheSame(oldItem: SongModel, newItem: SongModel): Boolean {
            return oldItem.songUri == newItem.songUri
        }

        override fun areContentsTheSame(oldItem: SongModel, newItem: SongModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            SongLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = differ.currentList[position]

        holder.binding.apply {
            tvDuration.text = currentSong.songDuration
            val drop = currentSong.songUri
            songTitle.text = drop?.replace("/storage/emulated/0/Music/", "")
            songArtist.text = currentSong.songArtist
            tvOrder.text = "${position + 1}"
        }

        holder.itemView.setOnClickListener {
            val direction =
                ListMusicFragmentDirections.actionListMusicFragmentToMusicPlayFragment(currentSong)
            it.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}