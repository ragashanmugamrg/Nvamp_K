package com.amp.nvamp.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.amp.nvamp.NvampApplication
import com.amp.nvamp.data.Song
import com.amp.nvamp.playback.PlaybackService
import com.amp.nvamp.utils.NvampUtils
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val storageRepo = StorageRepository(application)

    companion object {
        var songs = mutableListOf<Song>()
        var mediaitems = mutableListOf<MediaItem>()

        var deviceMusicByAlbum: Map<String, List<Song>> = mutableMapOf()
        var deviceMusicByFolder: Map<String, List<Song>> = mutableMapOf()
        var deviceMusicByArtist: Map<String, List<Song>> = mutableMapOf()
        var deviceMusicByGener: Map<String, List<Song>> = mutableMapOf()

        var deviceMusicByDate: MutableList<Song> = mutableListOf()

        var playListMusic: Map<String, List<Song>> = mutableMapOf()

        var lastplayedposition: Int = 0
    }

    val controllerFuture =
        MediaController.Builder(
            application,
            SessionToken(application, ComponentName(application, PlaybackService::class.java)),
        ).buildAsync()

    lateinit var controller: MediaController

    init {
        controllerFuture.addListener({
            controller = controllerFuture.get()
        }, MoreExecutors.directExecutor())
    }

    @OptIn(UnstableApi::class)
    suspend fun refreshdatainpref() {
        withContext(Dispatchers.IO) {
            dataIniziser()
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun initialized() {
        withContext(Dispatchers.IO) {
            songs = storageRepo.getAllSongs().toMutableList()
            if (songs.isNotEmpty()) {
                songs.forEach { data ->
                    val mediaItem = NvampUtils().changeSongmodeltoMediaitem(data)
                    mediaitems.add(mediaItem.build())
                }
            } else {
                dataIniziser()
            }
            updateCategorize(songs)
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun dataIniziser() {
        val contentResolver: ContentResolver = NvampApplication.context.contentResolver
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        var count = 0

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection =
            arrayOf(
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
                MediaStore.Audio.Media.DATE_MODIFIED,
            )
        val cursor =
            contentResolver.query(
                uri,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER,
            )

        songs.clear()
        mediaitems.clear()

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

                val file = File(data)
                if (adddate.equals(0)) {
                    adddate =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
                }

                val foldername = data.replace(display_name, "")

                var lastmodifydate = file.lastModified()

                val artworkUri = Uri.parse("content://media/external/audio/albumart")
                val imgUri =
                    ContentUris.withAppendedId(
                        artworkUri,
                        album_id,
                    )

                count += 1

                val pathFile = data?.let { it -> File(it) }

                val song =
                    Song(
                        title,
                        artist,
                        duration,
                        data,
                        album,
                        foldername,
                        album_id,
                        imgUri,
                        year,
                        gener ?: "Unknown",
                        id,
                        adddate,
                        count,
                        lastmodifydate,
                    )
                songs.add(song)

                val mediaItem = NvampUtils().changeSongmodeltoMediaitem(song)
                mediaitems.add(mediaItem.build())
            }
        }
        updateCategorize(songs)
        storageRepo.saveAllSongs(songs)
    }

    fun updateCategorize(songs: MutableList<Song>) {
        deviceMusicByAlbum = songs.groupBy { it.album }
        deviceMusicByFolder = songs.groupBy { it.foldername }
        deviceMusicByArtist = songs.groupBy { it.artist }
        deviceMusicByDate = songs.sortedByDescending { it.date }.toMutableList()
        deviceMusicByGener = songs.groupBy { it.gener ?: "Unknown" }
    }

    fun putlastPlayedMediaItem(media: List<Song>)  {
        StoragePrefrence().putLastPlayedMedia(media)
    }

    fun getlastPlayedMediaItem(): List<Song>  {
        return StoragePrefrence().getLastPlayedMedia()
    }

    fun setlastplayedpos(value: Int) {
        StoragePrefrence().putLastplayedpos(value)
    }

    fun setLastPlayedms(value: Float) {
        StoragePrefrence().putLastPlayedms(value)
    }

    fun getLastPlayedms(): Float {
        return StoragePrefrence().getLastPlayedms()!!
    }

    fun getlastplayedpos(): Int {
        lastplayedposition = StoragePrefrence().getLastplayedpos()
        return lastplayedposition
    }

    fun setplayListMusic(playList: Map<String, List<Song>>) {
        viewModelScope.launch {
            StoragePrefrence().putplayListMusic(playList)
        }
    }

    fun getplayListMusic(): Map<String, List<Song>> {
        viewModelScope.launch {
            playListMusic = StoragePrefrence().getplayListMusic()
        }
        return playListMusic
    }

    fun saveDarkMode(mode: Int) {
        StoragePrefrence().saveMode(mode)
    }
}
