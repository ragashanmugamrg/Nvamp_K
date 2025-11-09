package com.amp.nvamp.storagesystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amp.nvamp.storagesystem.data.SongEntity

@Dao
interface SongDao {

    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSongs(songs: List<SongEntity>)

    @Query("Delete from songs")
    suspend fun deleteAlltheSongs()
}