package com.amp.nvamp.data

import android.graphics.drawable.Drawable
import android.net.Uri
import com.squareup.moshi.Json

data class Song(
    @Json(name = "title")val title: String,
    @Json(name = "artist")val artist: String,
    @Json(name = "duration")val duration: Long?,
    @Json(name = "data")val data: String,
    @Json(name = "album")val album: String,
    @Json(name = "foldername")val foldername: String,
    @Json(name = "album_id")val album_id: Long,
    @Json(name = "imgUri")val imgUri: Uri
)


data class Album(
    val title: String,
    val artist: String,
    val cover: Drawable?,
    val songList: List<Song>
)

data class Artist(
    val artist: String,
    val songList: List<Song>
)