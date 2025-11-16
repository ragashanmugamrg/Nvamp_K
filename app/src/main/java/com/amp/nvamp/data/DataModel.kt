package com.amp.nvamp.data

import android.graphics.drawable.Drawable
import android.net.Uri
import com.amp.nvamp.storagesystem.data.SongEntity
import com.squareup.moshi.Json

data class Song(
    @Json(name = "title") val title: String,
    @Json(name = "artist") val artist: String,
    @Json(name = "duration") val duration: Long?,
    @Json(name = "data") val data: String,
    @Json(name = "album") val album: String,
    @Json(name = "foldername") val foldername: String,
    @Json(name = "album_id") val album_id: Long,
    @Json(name = "imgUri") val imgUri: Uri,
    @Json(name = "year") val year: String?,
    @Json(name = "gener") val gener: String?,
    @Json(name = "id") val id: String,
    @Json(name = "date") val date: Int?,
    @Json(name = "count") val count: Int?,
    @Json(name = "lastmodifiydate") val lastmodifiydate: Long?
)



fun SongEntity.toSongs(): Song {
    return Song(
        id = this.id,
        title = this.title,
        artist = this.artist,
        album = this.album,
        duration = this.duration ?: 0L,
        data = this.data,
        foldername = this.folder_name,
        album_id = this.album_id,
        imgUri = this.imgUri,
        year = this.year ?: "",
        gener = this.gener ?: "",
        date = this.date ?: 0,
        count = this.count ?: 0,
        lastmodifiydate = this.last_modifiy_dt ?: 0L
    )
}


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

data class Playlistdata(
    @Json val title: String,
    @Json val songid: String?
)