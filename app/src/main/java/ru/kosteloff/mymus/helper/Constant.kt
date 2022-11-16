package ru.kosteloff.mymus.helper

import android.content.Context
import android.widget.Toast
import java.util.concurrent.TimeUnit

object Constant {
        const val REQUEST_CODE_FOR_PERMISSIONS = 123

        fun Context.toast(text: String) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }

    fun durationConverter(duration: Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(duration)
                    )
        )
    }
}