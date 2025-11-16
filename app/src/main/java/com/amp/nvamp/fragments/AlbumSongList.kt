package com.amp.nvamp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.playerViewModel
import com.amp.nvamp.adapter.Songslistadapter
import com.amp.nvamp.data.Song
import com.amp.nvamp.databinding.FragmentAlbumSongListBinding
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByAlbum
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByArtist
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByDate
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByFolder
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByGener
import java.io.File


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
        }else if(fromfragmentname == "artist"){
            argname = requireArguments().getString("artistname")
            listofsongs = deviceMusicByArtist.get(argname)
        }else if(fromfragmentname == "gener"){
            argname = requireArguments().getString("genername")
            listofsongs = deviceMusicByGener.get(argname)
        }else if(fromfragmentname == "playlist"){
            argname = requireArguments().getString("playlistname")
            val deviceMusicByPlaylist = playerViewModel.getplayListMusic()
            listofsongs = deviceMusicByPlaylist.get(argname)
        }else if(fromfragmentname == "Recently Added"){
            argname = requireArguments().getString("playlistname")
            listofsongs = deviceMusicByDate
        }


        var listofAlbumsongs = changeSongmodeltoMediaitem(listofsongs)

        libraryListView = albumsonglistbinding?.albumsongrecyclerview

        albumsonglistbinding?.toolbar?.title = argname

        val layoutManager = LinearLayoutManager(requireContext())
        libraryListView?.layoutManager = layoutManager
        adapter = Songslistadapter(listofAlbumsongs, playerViewModel.controllerFuture)
        libraryListView?.adapter = adapter

        return albumsonglistbinding?.root
    }


    @OptIn(UnstableApi::class)
    fun changeSongmodeltoMediaitem(listofsongs: List<Song>?): MutableList<MediaItem>{
        var mediasongs = mutableListOf<MediaItem>()
        //playerViewModel.setlastplayedmedia(listofsongs!!.toMutableList())
        if (listofsongs!!.isNotEmpty()) {
            listofsongs.forEach { data ->
                val mediaItem = MediaItem.Builder().setMediaId(data.data)
                    .setUri((data.data.let { it -> File(it) }).toUri())
                    .setMediaId("MediaStore:$data.id")
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(data.title)
                            .setArtist(data.artist)
                            .setDurationMs(data.duration)
                            .setArtworkUri(data.imgUri)
                            .setDescription(data.data)
                            .build()
                    )
                mediasongs.add(mediaItem.build())
            }
        }

        return mediasongs
    }
}