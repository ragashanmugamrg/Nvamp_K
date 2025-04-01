package com.amp.nvamp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.MainActivity.Companion.mediaitems
import com.amp.nvamp.adapter.Songslistadapter
import com.amp.nvamp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    companion object{
        var libraryListView: RecyclerView? = null
        lateinit var adapter: Songslistadapter
    }


    var homebinding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homebinding = FragmentHomeBinding.inflate(inflater)

        libraryListView = homebinding?.recyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        libraryListView?.layoutManager = layoutManager
        adapter = Songslistadapter(mediaitems,medcontroller)
        libraryListView?.adapter = adapter
        
        return homebinding?.root
    }
}