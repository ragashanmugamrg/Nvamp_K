package com.amp.nvamp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.adapter.Artistlistadapter
import com.amp.nvamp.data.Song
import com.amp.nvamp.databinding.FragmentArtistBinding
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByArtist
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByFolder


class ArtistFragment : Fragment() {

    private lateinit var artistbinding: FragmentArtistBinding
    var artistListView: RecyclerView? = null

    companion object{
        private val deviceMusicList: MutableMap<String, List<Song>> = mutableMapOf()
        private lateinit var adapter: Artistlistadapter
        fun playernotify(){
            playernotifyadapter()
        }

        private fun playernotifyadapter(){
            if (deviceMusicByArtist.isNotEmpty()){
                deviceMusicList.clear()
                deviceMusicList.putAll(deviceMusicByArtist)
                adapter.notifyItemRangeChanged(1,10)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        artistbinding = FragmentArtistBinding.inflate(layoutInflater)

        artistListView = artistbinding.artistsongrecyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        artistListView?.layoutManager = layoutManager
        adapter = Artistlistadapter(deviceMusicList, medcontroller)
        artistListView?.adapter = adapter
        return artistbinding.root
    }
}