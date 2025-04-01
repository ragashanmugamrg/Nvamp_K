package com.amp.nvamp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.deviceMusicByAlbum
import com.amp.nvamp.MainActivity.Companion.deviceMusicByFolder
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.Song
import com.amp.nvamp.adapter.Songslistadapter
import com.amp.nvamp.databinding.FragmentAlbumSongListBinding


class AlbumSongList : Fragment() {

    var position: Int? = null
    var argname: String? = null

    var fromfragmentname: String? = null

    var listofsongs: List<Song>? = null


    var libraryListView: RecyclerView? = null
    lateinit var adapter: Songslistadapter

    var albumsonglistbinding: FragmentAlbumSongListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        albumsonglistbinding = FragmentAlbumSongListBinding.inflate(layoutInflater)


        position = requireArguments().getInt("position")
        fromfragmentname = requireArguments().getString("fromfragment")

        if (fromfragmentname == "folder"){
            argname = requireArguments().getString("foldername")
            listofsongs = deviceMusicByFolder.get(argname)
        }else if (fromfragmentname == "album"){
            argname = requireArguments().getString("albumname")
            listofsongs = deviceMusicByAlbum.get(argname)
        }


        var listofAlbumsongs = changeSongmodeltoMediaitem(listofsongs)

        libraryListView = albumsonglistbinding?.albumsongrecyclerview

        albumsonglistbinding?.toolbar?.title = argname

        val layoutManager = LinearLayoutManager(requireContext())
        libraryListView?.layoutManager = layoutManager
        adapter = Songslistadapter(listofAlbumsongs, medcontroller)
        libraryListView?.adapter = adapter




        return albumsonglistbinding?.root
    }


    fun changeSongmodeltoMediaitem(listofsongs: List<Song>?): MutableList<MediaItem>{
        var mediasongs = mutableListOf<MediaItem>()
        if (listofsongs!!.isNotEmpty()) {
            listofsongs.forEach { data ->
                val mediaItem = MediaItem.Builder().setMediaId(data.data)
                    .setUri(Uri.parse(data.data))
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(data.title)
                            .setArtist(data.artist)
                            .setArtworkUri(data.imgUri)
                            .build()
                    )
                mediasongs.add(mediaItem.build())
            }
        }

        return mediasongs
    }
}