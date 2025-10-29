package com.amp.nvamp.viewmodel

import android.net.Uri
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class UriParser {

    @FromJson
    fun fromJson(uri: String?): Uri? {
        return if (uri != null) Uri.parse(uri) else null
    }

    @ToJson
    fun toJson(uri: Uri?): String? {
        return uri?.toString()
    }
}