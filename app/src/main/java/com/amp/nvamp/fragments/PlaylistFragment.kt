package com.amp.nvamp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.MainActivity.Companion.playerViewModel
import com.amp.nvamp.R
import com.amp.nvamp.adapter.Folderlistadapter
import com.amp.nvamp.adapter.Playlistadapter
import com.amp.nvamp.databinding.FragmentFolderBinding
import com.amp.nvamp.databinding.FragmentPlaylistBinding
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByArtist
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByFolder
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.playListMusic


class PlaylistFragment : Fragment() {

    private lateinit var playlistbinding: FragmentPlaylistBinding
    var folderListView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val deviceMusicByPlaylist = playerViewModel.getplayListMusic()

        playlistbinding = FragmentPlaylistBinding.inflate(layoutInflater)

        folderListView = playlistbinding.playlistsongrecyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        folderListView?.layoutManager = layoutManager
        val adapter = Playlistadapter(deviceMusicByPlaylist, medcontroller)
        folderListView?.adapter = adapter
        adapter.notifyDataSetChanged()

        return playlistbinding.root
    }
}