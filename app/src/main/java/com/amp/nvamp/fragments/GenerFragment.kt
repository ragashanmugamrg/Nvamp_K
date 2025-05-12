package com.amp.nvamp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.adapter.Generlistadapter
import com.amp.nvamp.data.Song
import com.amp.nvamp.databinding.FragmentGenerBinding
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByFolder
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByGener


class GenerFragment : Fragment() {

    private lateinit var generbinding: FragmentGenerBinding
    var generListView: RecyclerView? = null

    companion object{
        private val deviceMusicList: MutableMap<String, List<Song>> = mutableMapOf()
        private lateinit var adapter: Generlistadapter
        fun playernotify(){
            playernotifyadapter()
        }

        private fun playernotifyadapter(){
            if (deviceMusicByGener.isNotEmpty()){
                deviceMusicList.clear()
                deviceMusicList.putAll(deviceMusicByGener)
                adapter.notifyItemRangeChanged(1,10)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        generbinding = FragmentGenerBinding.inflate(layoutInflater)

        generListView = generbinding.foldersongrecyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        generListView?.layoutManager = layoutManager
        adapter = Generlistadapter(deviceMusicList, medcontroller)
        generListView?.adapter = adapter

        return generbinding.root
    }
}