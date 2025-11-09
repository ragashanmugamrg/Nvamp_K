package com.amp.nvamp.storagesystem

import android.net.Uri
import androidx.room.TypeConverter

class TypeConvertor {
    @TypeConverter
    fun fromUri(uri: Uri?):String?{
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uri: String?): Uri?{
        return Uri.parse(uri) ?: null
    }
}