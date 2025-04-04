package com.amp.nvamp.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.amp.nvamp.NvampApplication
import com.amp.nvamp.data.Album
import com.amp.nvamp.data.Song
import com.amp.nvamp.fragments.HomeFragment

class PlayerViewModel(application: Application) : AndroidViewModel(application){

    var songs = mutableListOf<Song>()
    companion object{
        var mediaitems = mutableListOf<MediaItem>()

        lateinit var deviceMusicByAlbum: Map<String, List<Song>>
        lateinit var deviceMusicByFolder: Map<String, List<Song>>
        lateinit var deviceMusicByArtist: Map<String, List<Song>>
        lateinit var customFragmentManager: FragmentManager
    }


    @OptIn(UnstableApi::class)
    fun initialized(){
        songs = StoragePrefrence().getsongdata()
        if(songs.isNotEmpty()){
            songs.forEach{
                data -> val mediaItem = MediaItem.Builder().setMediaId(data.data)
                .setUri(Uri.parse(data.data))
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(data.title)
                        .setArtist(data.artist)
                        .setDurationMs(data.duration)
                        .setArtworkUri(data.imgUri)
                        .build()
                )
                mediaitems.add(mediaItem.build())
            }
        }else{
            dataIniziser()
            StoragePrefrence().putsongdata(songs)
        }



        deviceMusicByAlbum = songs
            .groupBy { it.album }

        deviceMusicByFolder = songs.groupBy {
                it.foldername
        }
        HomeFragment.playernotify()
    }


    @OptIn(UnstableApi::class)
    public fun dataIniziser() {
        val contentResolver: ContentResolver = NvampApplication.context.contentResolver


        val album = mutableListOf<Album>()


        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DISPLAY_NAME
        )
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
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
                val display_name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val foldername = data.replace(display_name, "")

                val artworkUri = Uri.parse("content://media/external/audio/albumart")
                val imgUri = ContentUris.withAppendedId(
                    artworkUri,
                    album_id
                )

                val song = Song(title,artist,duration,data,album,foldername,album_id,imgUri)
                songs.add(song)

                val mediaItem = MediaItem.Builder().setMediaId(data)
                    .setUri(Uri.parse(data))
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(title)
                            .setArtist(artist)
                            .setArtworkUri(imgUri)
                            .setDurationMs(duration)
                            .build()
                    )

                mediaitems.add(mediaItem.build())
            }
        }


        deviceMusicByAlbum = songs
            .groupBy { it.album }

        deviceMusicByFolder = songs.groupBy {
            it.foldername
        }
    }

}