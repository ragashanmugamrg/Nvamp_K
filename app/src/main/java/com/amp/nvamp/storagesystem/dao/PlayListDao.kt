package com.amp.nvamp.storagesystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.amp.nvamp.storagesystem.data.PlaylistCrossRef
import com.amp.nvamp.storagesystem.data.PlaylistEntity
import com.amp.nvamp.storagesystem.data.PlaylistWithsongs
import com.amp.nvamp.storagesystem.data.SongEntity


@Dao
interface PlayListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayList(playlist: PlaylistEntity):Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistCrossref(playlistCrossRef: List<PlaylistCrossRef>)


    @Transaction
    @Query("Select * from playlist")
    suspend fun getPlayListWithSongs(): List<PlaylistWithsongs>

    @Query("SELECT * FROM songs WHERE id IN (:ids)")
    suspend fun getSongsByIds(ids: List<String?>): List<SongEntity>

}