package com.amp.nvamp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.media3.common.MediaItem
import com.amp.nvamp.NvampApplication
import com.amp.nvamp.data.Song
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException
import java.lang.reflect.ParameterizedType


class StoragePrefrence {

    var sp:SharedPreferences? = null
    var moshi: Moshi? = null

    var types:ParameterizedType? = null


    init {
        sp = NvampApplication.context.getSharedPreferences("PlayerPreferences", Context.MODE_PRIVATE)
        moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(UriParser())
            .build()

        types = Types.newParameterizedType(MutableList::class.java,Song::class.java)
    }


    fun putLastplayedpos(lastplayedposition: Int){
        val position = sp!!.edit().putInt("lastplayedpos", lastplayedposition).apply()
    }

    fun getLastplayedpos(): Int{
        return sp!!.getInt("lastplayedpos", 0)
    }


    fun putlastplayed(value: MutableList<MediaItem>?) {
        val jsonAdapter = types?.let { moshi?.adapter<List<MediaItem>>(it) }
        val json = jsonAdapter?.toJson(value)
        sp!!.edit().putString("lastsongdata", json).apply()
    }


    fun getlastplayed(): MutableList<MediaItem> {
        val json = sp!!.getString("lastsongdata", null)
        if (json != null) {
            try {
                val jsonAdapter = types?.let { moshi?.adapter<List<MediaItem>>(it) }
                return (jsonAdapter?.fromJson(json) ?: mutableListOf()).toMutableList()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        return mutableListOf()
    }


    fun putsongdata(value: MutableList<Song>?) {
        val jsonAdapter =
            types?.let { moshi?.adapter<List<Song>>(it) }
        val json = jsonAdapter?.toJson(value)
        sp!!.edit().putString("songdata", json).apply()
    }

    fun getsongdata(): MutableList<Song> {
        val json = sp!!.getString("songdata", null)
        if (json != null) {
            try {
                val jsonAdapter = types?.let { moshi?.adapter<List<Song>>(it) }
                return (jsonAdapter?.fromJson(json) ?: mutableListOf()).toMutableList()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        return mutableListOf()
    }

}