package com.amp.nvamp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.adapter.Albumlistadapter
import com.amp.nvamp.data.Song
import com.amp.nvamp.databinding.FragmentMusicLibraryBinding
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByAlbum

class
MusicLibrary : Fragment() {

    companion object{
        var libraryListView: RecyclerView? = null
        private lateinit var adapter: Albumlistadapter

        private val deviceMusicList: MutableMap<String, List<Song>> = mutableMapOf()

        fun playernotify(){
            playernotifyadapter()
        }

        private fun playernotifyadapter(){
            if (deviceMusicByAlbum.isNotEmpty()){
                deviceMusicList.clear()
                deviceMusicList.putAll(deviceMusicByAlbum)
                adapter.notifyItemRangeChanged(1,10)
            }
        }
    }


    var homebinding: FragmentMusicLibraryBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homebinding = FragmentMusicLibraryBinding.inflate(inflater)

        libraryListView = homebinding?.recyclerviewalbum
        val layoutManager = GridLayoutManager(requireContext(),2)
        libraryListView?.layoutManager = layoutManager
        adapter = Albumlistadapter(deviceMusicList, medcontroller)
        libraryListView?.adapter = adapter

        return homebinding?.root
    }
}