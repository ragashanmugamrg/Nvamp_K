package com.amp.nvamp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amp.nvamp.fragments.FolderFragment
import com.amp.nvamp.fragments.HomeFragment
import com.amp.nvamp.fragments.MusicLibrary

class TabLayout(fragment: FragmentActivity): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return HomeFragment()
            1 -> return MusicLibrary()
            2 -> return FolderFragment()
            else -> return throw IllegalStateException("Hello....")

        }
    }
}