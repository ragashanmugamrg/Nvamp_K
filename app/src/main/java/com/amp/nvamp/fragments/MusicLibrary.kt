package com.amp.nvamp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.deviceMusicByAlbum
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.adapter.Albumlistadapter
import com.amp.nvamp.databinding.FragmentMusicLibraryBinding

class MusicLibrary : Fragment() {

    companion object{
        var libraryListView: RecyclerView? = null
        lateinit var adapter: Albumlistadapter
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
        adapter = Albumlistadapter(deviceMusicByAlbum, medcontroller)
        libraryListView?.adapter = adapter

        return homebinding?.root
    }
}