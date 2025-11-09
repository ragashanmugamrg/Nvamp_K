package com.amp.nvamp.storagesystem

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amp.nvamp.data.Song


abstract class AppDatabase: RoomDatabase() {
    abstract fun songDao(): Song
    abstract fun lastplayedDao()
    abstract fun playlistDao()
}