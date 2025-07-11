package com.amp.nvamp.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.amp.nvamp.NvampApplication
import com.amp.nvamp.data.Album
import com.amp.nvamp.data.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    var songs = mutableListOf<Song>()

    companion object {
        var mediaitems = mutableListOf<MediaItem>()

        var deviceMusicByAlbum: Map<String, List<Song>> = mutableMapOf()
        var deviceMusicByFolder: Map<String, List<Song>> = mutableMapOf()
        var deviceMusicByArtist: Map<String, List<Song>> = mutableMapOf()
        var deviceMusicByGener: Map<String, List<Song>> = mutableMapOf()

        var deviceMusicByDate: MutableList<Song> = mutableListOf()

        var lastPlayedMusic = mutableListOf<MediaItem>()
        var playListMusic: Map<String, List<Song>> = mutableMapOf()

        var lastplayedposition: Int = 0

        lateinit var customFragmentManager: FragmentManager
    }

    fun setlastplayedpos(value: Int) {
        StoragePrefrence().putLastplayedpos(value)
    }

    fun getlastplayedpos(): Int {
        return StoragePrefrence().getLastplayedpos()
    }


    fun setlastplayedmedia(lastPlayedMusic: MutableList<Song>) {
        StoragePrefrence().putlastplayed(lastPlayedMusic)
    }

    fun getlastplayedmedia(): MutableList<Song> {
        return StoragePrefrence().getlastplayed()
    }


    fun setplayListMusic(lastPlayedMusic: Map<String, List<Song>>) {
        playListMusic = lastPlayedMusic
        StoragePrefrence().putplayListMusic(lastPlayedMusic)
    }

    fun getplayListMusic(): Map<String, List<Song>> {
        return StoragePrefrence().getplayListMusic()
    }

    @OptIn(UnstableApi::class)
    suspend fun refreshdatainpref() {
        withContext(Dispatchers.IO) {
            dataIniziser()
            StoragePrefrence().putsongdata(songs)
        }
    }


    @OptIn(UnstableApi::class)
    suspend fun initialized() {
        withContext(Dispatchers.IO) {
            songs = StoragePrefrence().getsongdata()
            if (songs.isNotEmpty()) {
                songs.forEach { data ->
                    val mediaItem = MediaItem.Builder().setMediaId(data.data)
                        .setUri((data.data.let { File(it) }).toUri())
                        .setMediaId("MediaStore:$data.id")
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTrackNumber(data.count)
                                .setTitle(data.title)
                                .setArtist(data.artist)
                                .setDurationMs(data.duration)
                                .setArtworkUri(data.imgUri)
                                .setGenre(data.gener)
                                .setDescription(data.data)
                                .build()
                        )
                    mediaitems.add(mediaItem.build())
                }
            } else {
                dataIniziser()
                StoragePrefrence().putsongdata(songs)
            }


            deviceMusicByAlbum = songs.groupBy {
                it.album
            }

            deviceMusicByFolder = songs.groupBy {
                it.foldername
            }

            deviceMusicByArtist = songs.groupBy {
                it.artist
            }

            deviceMusicByGener = songs.groupBy {
                it.gener.toString()
            }

            deviceMusicByDate = songs
                .sortedByDescending { it.date }
                .toMutableList()

            lastplayedposition = 0
        }
    }


    @OptIn(UnstableApi::class)
    fun dataIniziser() {
        val contentResolver: ContentResolver = NvampApplication.context.contentResolver
        val album = mutableListOf<Album>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        var count = 0

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.GENRE,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED
        )
        val cursor = contentResolver.query(
            uri,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )

        songs.clear()

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val data =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val album =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val album_id =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val duration =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                var year =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
                val display_name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val gener =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE))
                var adddate =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))

                if (adddate == 0)
                    adddate = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))

                val foldername = data.replace(display_name, "")

                val artworkUri = Uri.parse("content://media/external/audio/albumart")
                val imgUri = ContentUris.withAppendedId(
                    artworkUri,
                    album_id
                )

                count += 1

                println(adddate)

                val pathFile = data?.let { it -> File(it) }

                val song = Song(
                    title,
                    artist,
                    duration,
                    data,
                    album,
                    foldername,
                    album_id,
                    imgUri,
                    year,
                    gener,
                    id,
                    adddate,
                    count
                )
                songs.add(song)


                val mediaItem = MediaItem.Builder().setMediaId(data)
                    .setUri(pathFile?.toUri())
                    .setMediaId("MediaStore:$id")
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTrackNumber(count)
                            .setTitle(title)
                            .setArtist(artist)
                            .setArtworkUri(imgUri)
                            .setDurationMs(duration)
                            .setGenre(gener)
                            .setDescription(data)
                            .build()
                    )

                mediaitems.add(mediaItem.build())
            }
        }

        deviceMusicByAlbum = songs.groupBy {
            it.album
        }

        deviceMusicByFolder = songs.groupBy {
            it.foldername
        }

        deviceMusicByArtist = songs.groupBy {
            it.artist
        }

        deviceMusicByDate = songs.sortedByDescending { it.date }
            .toMutableList()


        deviceMusicByGener = songs.groupBy { it.gener.toString() }
    }

}