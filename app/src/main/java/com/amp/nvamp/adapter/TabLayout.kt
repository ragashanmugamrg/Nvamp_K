package com.amp.nvamp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amp.nvamp.fragments.ArtistFragment
import com.amp.nvamp.fragments.FolderFragment
import com.amp.nvamp.fragments.GenerFragment
import com.amp.nvamp.fragments.HomeFragment
import com.amp.nvamp.fragments.MusicLibrary
import com.amp.nvamp.fragments.PlaylistFragment

class TabLayout(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return HomeFragment()
            1 -> return MusicLibrary()
            2 -> return FolderFragment()
            3 -> return ArtistFragment()
            4 -> return GenerFragment()
            5 -> return PlaylistFragment()
            else -> return throw IllegalStateException("Hello....")
        }
    }
}
