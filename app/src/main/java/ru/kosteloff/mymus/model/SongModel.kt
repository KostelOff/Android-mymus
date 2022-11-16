package ru.kosteloff.mymus.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SongModel(
    val songTitle: String?,
    val songArtist: String?,
    val songUri: String?,
    val songDuration: String?
): Parcelable
