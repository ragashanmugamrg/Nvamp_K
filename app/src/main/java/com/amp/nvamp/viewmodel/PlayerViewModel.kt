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
import com.amp.nvamp.fragments.HomeFragment
import com.amp.nvamp.fragments.MusicLibrary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PlayerViewModel(application: Application) : AndroidViewModel(application){

    var songs = mutableListOf<Song>()
    companion object{
        var mediaitems = mutableListOf<MediaItem>()

        var deviceMusicByAlbum: Map<String, List<Song>> = mutableMapOf()
        var deviceMusicByFolder: Map<String, List<Song>> = mapOf()
        var deviceMusicByArtist: Map<String, List<Song>> = mapOf()
        var deviceMusicByGener: Map<String, List<Song>> = mapOf()

        var lastPlayedMusic = mutableListOf<MediaItem>()
        lateinit var playListMusic: Map<String, List<Song>>

        var lastplayedposition: Int = 0

        lateinit var customFragmentManager: FragmentManager
    }


    fun setlastplayedmedia(lastPlayedMusic: MutableList<MediaItem>){
        StoragePrefrence().putlastplayed(lastPlayedMusic)
    }

    fun getlastplayedmedia():MutableList<MediaItem>{
        return StoragePrefrence().getlastplayed()
    }


    @OptIn(UnstableApi::class)
    suspend  fun initialized(){
        withContext(Dispatchers.IO){
            songs = StoragePrefrence().getsongdata()
            if(songs.isNotEmpty()){
                songs.forEach{
                        data -> val mediaItem = MediaItem.Builder().setMediaId(data.data)
                    .setUri((data.data.let { it -> File(it) }).toUri())
                    .setMediaId("MediaStore:$data.id")
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(data.title)
                            .setArtist(data.artist)
                            .setDurationMs(data.duration)
                            .setArtworkUri(data.imgUri)
                            .setGenre(data.gener)
                            .build()
                    )
                    mediaitems.add(mediaItem.build())
                }
            }else{
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

            lastplayedposition = 0
       }
    }


    @OptIn(UnstableApi::class)
    public fun dataIniziser() {
        val contentResolver: ContentResolver = NvampApplication.context.contentResolver
        val album = mutableListOf<Album>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

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
            MediaStore.Audio.Media.ALBUM_ARTIST
        )
        val cursor = contentResolver.query(
            uri,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )

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
                val foldername = data.replace(display_name, "")

                val artworkUri = Uri.parse("content://media/external/audio/albumart")
                val imgUri = ContentUris.withAppendedId(
                    artworkUri,
                    album_id
                )

                val pathFile = data?.let { it -> File(it) }

                val song = Song(title,artist,duration,data,album,foldername,album_id,imgUri,year,gener,id)
                songs.add(song)

                val mediaItem = MediaItem.Builder().setMediaId(data)
                    .setUri(pathFile?.toUri())
                    .setMediaId("MediaStore:$id")
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(title)
                            .setArtist(artist)
                            .setArtworkUri(imgUri)
                            .setDurationMs(duration)
                            .setGenre(gener)
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

        deviceMusicByGener = songs.groupBy {
            it.gener.toString()
        }

    }

}