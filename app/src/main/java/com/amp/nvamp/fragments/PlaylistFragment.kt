package com.amp.nvamp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.MainActivity.Companion.playerViewModel
import com.amp.nvamp.adapter.Playlistadapter
import com.amp.nvamp.data.Song
import com.amp.nvamp.databinding.FragmentPlaylistBinding
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByDate


class PlaylistFragment : Fragment() {

    private lateinit var playlistbinding: FragmentPlaylistBinding
    var folderListView: RecyclerView? = null



    companion object{
        private val deviceMusicList: MutableMap<String, List<Song>> = mutableMapOf()
        private lateinit var adapter: Playlistadapter
        fun playernotify(){
            playernotifyadapter()
        }

        private fun playernotifyadapter(){
            val deviceMusicByPlaylist = playerViewModel.getplayListMusic()
            if (deviceMusicByPlaylist.isNotEmpty()){
                deviceMusicList.putAll(deviceMusicByPlaylist)
                adapter.notifyItemRangeChanged(1,10)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val deviceMusicByPlaylist = playerViewModel.getplayListMusic()

        deviceMusicList.put("Recently Added", deviceMusicByDate)
        deviceMusicList.putAll(deviceMusicByPlaylist)

        playlistbinding = FragmentPlaylistBinding.inflate(layoutInflater)

        folderListView = playlistbinding.playlistsongrecyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        folderListView?.layoutManager = layoutManager
        adapter = Playlistadapter(deviceMusicList, medcontroller)
        folderListView?.adapter = adapter

        return playlistbinding.root
    }
}