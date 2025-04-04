package com.amp.nvamp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.adapter.Folderlistadapter
import com.amp.nvamp.databinding.FragmentFolderBinding
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.deviceMusicByFolder


class FolderFragment : Fragment() {

    private lateinit var folderbinding: FragmentFolderBinding
    var folderListView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        folderbinding = FragmentFolderBinding.inflate(layoutInflater)

        folderListView = folderbinding.foldersongrecyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        folderListView?.layoutManager = layoutManager
        val adapter = Folderlistadapter(deviceMusicByFolder, medcontroller)
        folderListView?.adapter = adapter

        return folderbinding.root
    }
}