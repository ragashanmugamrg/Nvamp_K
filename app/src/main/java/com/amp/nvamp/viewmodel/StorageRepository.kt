package com.amp.nvamp.viewmodel

import android.content.Context
import com.amp.nvamp.data.Song
import com.amp.nvamp.storagesystem.DatabaseProvider
import com.amp.nvamp.storagesystem.data.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StorageRepository(context: Context) {
    private val db = DatabaseProvider.getInstance(context)
    private val songDao = db.songDao()
    //private val playlistDao = db.playlistDao()
    //private val db = DatabaseProvider.getInstance(context)


    suspend fun saveAllSongs(songs: List<Song>) = withContext(Dispatchers.IO){
        val entities = songs.map {
            SongEntity(
                title = it.title,
                artist = it.artist,
                duration = it.duration,
                data = it.data,
                album = it.album,
                folder_name = it.foldername,
                album_id = it.album_id,
                imgUri = it.imgUri,
                year = it.year,
                gener = it.gener,
                id = it.id,
                date = it.date,
                count = it.count,
                last_modifiy_dt = it.lastmodifiydate
            )
        }
        songDao.deleteAlltheSongs()
        songDao.insertAllSongs(entities)
    }

    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO){
        songDao.getAllSongs().map {
            Song(
                title = it.title,
                artist = it.artist,
                duration = it.duration,
                data = it.data,
                album = it.album,
                foldername = it.folder_name,
                album_id = it.album_id,
                imgUri = it.imgUri,
                year = it.year,
                gener = it.gener,
                id = it.id,
                date = it.date,
                count = it.count,
                lastmodifiydate = it.last_modifiy_dt
            )
        }
    }


}