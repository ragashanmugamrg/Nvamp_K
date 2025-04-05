package com.amp.nvamp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.amp.nvamp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ViewPagerFragment : Fragment() {


    private lateinit var fragmentContainer: ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = view.findViewById(R.id.tab_layout)
        fragmentContainer = view.findViewById(R.id.fragmentcontainer)

        fragmentContainer.adapter = com.amp.nvamp.adapter.TabLayout(requireActivity())

        fragmentContainer.offscreenPageLimit = 1

        TabLayoutMediator(tabLayout,fragmentContainer){
                tab, position ->
            tab.text = when(position){
                0 -> "Songs"
                1 -> "Albums"
                2 -> "Folder"
                3 -> "Artist"
                4 -> "Gener"
                5 -> "Playlist"
                else -> "Unknown"
            }
        }.attach()
    }
}