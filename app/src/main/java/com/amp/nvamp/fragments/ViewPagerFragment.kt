package com.amp.nvamp.fragments

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.amp.nvamp.MainActivity
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.R
import com.amp.nvamp.adapter.Folderlistadapter
import com.amp.nvamp.playback.PlaybackService.Companion.sessionId
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch


class ViewPagerFragment : Fragment() {


    private lateinit var fragmentContainer: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var toolbar: MaterialToolbar
    companion object{
        lateinit var loaderview: MaterialCardView
    }



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

        loaderview = view.findViewById(R.id.loaderview)

        toolbar = view.findViewById(R.id.toolbar)

        fragmentContainer.adapter = com.amp.nvamp.adapter.TabLayout(requireActivity())

        fragmentContainer.offscreenPageLimit = 6

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


        toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.title == "Refresh"){
                lifecycleScope.launch {
                    loaderview.visibility = VISIBLE
                    MainActivity.playerViewModel.refreshdatainpref()
                    HomeFragment.playernotify()
                    MusicLibrary.playernotify()
                    FolderFragment.playernotify()
                    ArtistFragment.playernotify()
                    GenerFragment.playernotify()
                    PlaylistFragment.playernotify()
                    loaderview.visibility = GONE
                }
                return@setOnMenuItemClickListener true
            }else if (menuItem.title == "Settings"){

            }else if (menuItem.title == "Equalizer"){
                val intent: Intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                    putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId)
                    putExtra(AudioEffect.EXTRA_PACKAGE_NAME, requireContext().packageName)
                    putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                }

                requireContext().startActivity(intent)

                return@setOnMenuItemClickListener true
            }

            return@setOnMenuItemClickListener false
        }

    }
}