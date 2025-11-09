package com.amp.nvamp.storagesystem.data;

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.amp.nvamp.data.Song

@Entity(
    tableName = "songs"
)
data class SongEntity(
    @PrimaryKey(autoGenerate = true) val songid: Int = 0,
    val title: String,
    val artist: String,
    val duration: Long?,
    val data: String,
    val album: String,
    val folder_name: String,
    val album_id: Long,
    val imgUri: Uri,
    val year: String?,
    val gener: String?,
    val id: String?,
    val date: Int?,
    val count: Int?,
    val last_modifiy_dt: Long?
)

fun Song.toEntity(): SongEntity {
    return SongEntity(
        id = this.id ?: "",
        title = this.title ?: "",
        artist = this.artist ?: "",
        album = this.album ?: "",
        duration = this.duration ?: 0L,
        data = this.data ?: "",
        folder_name = this.foldername ?: "",
        album_id = this.album_id ?: 0L,
        imgUri = this.imgUri?: Uri.EMPTY,
        year = this.year ?: "",
        gener = this.gener ?: "",
        date = this.date ?: 0,
        count = this.count ?: 0,
        last_modifiy_dt = this.lastmodifiydate ?: 0L
    )
}


@Entity(tableName = "playlist")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistid: Int = 0,
    val playlistname: String,
)


@Entity(tableName = "lastplayed")
data class lastPlayedEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val lastplayedsongs: SongEntity,
    val lastplayedsong: Int,
    val lastplayedpos: Long
)
