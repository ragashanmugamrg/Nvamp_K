package com.amp.nvamp.utils

import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.amp.nvamp.MainActivity.Companion.playerViewModel
import com.amp.nvamp.data.Song
import java.io.File
import java.util.concurrent.TimeUnit

class NvampUtils {

    fun formatDuration(duration: Long): String {
        val minutes: Long = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds: Long = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }

    @OptIn(UnstableApi::class)
    fun changeSongmodeltoMediaitem(listofsongs: List<Song>?): MutableList<MediaItem>{
        var mediasongs = mutableListOf<MediaItem>()
        playerViewModel.setlastplayedmedia(listofsongs!!.toMutableList())
        if (listofsongs!!.isNotEmpty()) {
            listofsongs.forEach { data ->
                val mediaItem = MediaItem.Builder().setMediaId(data.data)
                    .setUri((data.data.let { it -> File(it) }).toUri())
                    .setMediaId("MediaStore:$data.id")
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(data.title)
                            .setArtist(data.artist)
                            .setDurationMs(data.duration)
                            .setArtworkUri(data.imgUri)
                            .setDescription(data.data)
                            .build()
                    )
                mediasongs.add(mediaItem.build())
            }
        }

        return mediasongs
    }
}