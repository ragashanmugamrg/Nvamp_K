package com.amp.nvamp.storagesystem

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amp.nvamp.storagesystem.dao.PlayListDao
import com.amp.nvamp.storagesystem.dao.SongDao
import com.amp.nvamp.storagesystem.data.PlaylistCrossRef
import com.amp.nvamp.storagesystem.data.PlaylistEntity
import com.amp.nvamp.storagesystem.data.SongEntity

@Database(
    entities = [SongEntity::class, PlaylistEntity::class, PlaylistCrossRef::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(TypeConvertor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    abstract fun playlistDao(): PlayListDao
//    abstract fun playlistDao()
}
