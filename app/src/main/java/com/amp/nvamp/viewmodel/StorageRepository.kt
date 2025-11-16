package com.amp.nvamp.viewmodel

import android.content.Context
import com.amp.nvamp.data.Song
import com.amp.nvamp.data.toSongs
import com.amp.nvamp.storagesystem.DatabaseProvider
import com.amp.nvamp.storagesystem.data.PlaylistCrossRef
import com.amp.nvamp.storagesystem.data.PlaylistEntity
import com.amp.nvamp.storagesystem.data.SongEntity
import com.amp.nvamp.storagesystem.data.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StorageRepository(context: Context) {
    private val db = DatabaseProvider.getInstance(context)
    private val songDao = db.songDao()
    private val playlistDao = db.playlistDao()
    // private val db = DatabaseProvider.getInstance(context)

    suspend fun saveAllSongs(songs: List<Song>) =
        withContext(Dispatchers.IO) {
            val entities =
                songs.map {
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
                        last_modifiy_dt = it.lastmodifiydate,
                    )
                }
            songDao.deleteAlltheSongs()
            songDao.insertAllSongs(entities)
        }

    suspend fun getAllSongs(): List<Song> =
        withContext(Dispatchers.IO) {
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
                    lastmodifiydate = it.last_modifiy_dt,
                )
            }
        }

    suspend fun savealltheplaylist(playlist: Map<String, List<Song>>) =
        withContext(Dispatchers.IO) {
            playlist.forEach { (playlistName, songs) ->
                // Convert songs to entities
                val songEntities = songs.map { it.toEntity() }

                // First, insert all songs (REPLACE strategy handles duplicates)
                playlistDao.insertSongs(songEntities)

                // Insert the playlist and get its ID
                val playlistId =
                    playlistDao.insertPlayList(
                        PlaylistEntity(playlistname = playlistName),
                    ).toInt()

                // Only create cross references if playlist was successfully created
                if (playlistId > 0) {
                    val crossRefs =
                        songEntities.map { song ->
                            PlaylistCrossRef(
                                playlistid = playlistId,
                                id = song.id,
                            )
                        }
                    playlistDao.insertPlaylistCrossref(crossRefs)
                }
            }
        }

    suspend fun getalltheplaylist(): Map<String, List<Song>> =
        withContext(Dispatchers.IO) {
            val playlists = playlistDao.getPlayListWithSongs()
            playlists.associate { playlistWithSongs ->
                playlistWithSongs.playlist.playlistname to
                    playlistWithSongs.playlists.map {
                        it.toSongs()
                    }
            }
        }
}
