package com.amp.nvamp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.R
import com.amp.nvamp.adapter.Folderlistadapter
import com.amp.nvamp.adapter.Generlistadapter
import com.amp.nvamp.databinding.FragmentFolderBinding
import com.amp.nvamp.databinding.FragmentGenerBinding
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByFolder
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByGener


class GenerFragment : Fragment() {

    private lateinit var generbinding: FragmentGenerBinding
    var generListView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        generbinding = FragmentGenerBinding.inflate(layoutInflater)

        generListView = generbinding.foldersongrecyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        generListView?.layoutManager = layoutManager
        val adapter = Generlistadapter(deviceMusicByGener, medcontroller)
        generListView?.adapter = adapter

        return generbinding.root
    }
}