package com.amp.nvamp.fragments

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.viewpager2.widget.ViewPager2
import com.amp.nvamp.MainActivity
import com.amp.nvamp.MainActivity.Companion.medcontroller
import com.amp.nvamp.R
import com.amp.nvamp.adapter.Folderlistadapter
import com.amp.nvamp.adapter.Songslistadapter
import com.amp.nvamp.data.Song
import com.amp.nvamp.playback.PlaybackService.Companion.sessionId
import com.amp.nvamp.settings.NvampPlayerSettings
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.mediaitems
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.songs


class ViewPagerFragment : Fragment() {


    private lateinit var fragmentContainer: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var navigationview: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    lateinit var adapter: Songslistadapter
    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView

    private var allSongs: List<Song> = songs
    private var filteredSongs: MutableList<Song> = mutableListOf()

    companion object {
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

        drawerLayout = view.findViewById(R.id.drawer_layout)

        loaderview = view.findViewById(R.id.loaderview)

        searchBar = view.findViewById(R.id.search_bar)
        searchView = view.findViewById(R.id.search_view)

        toolbar = view.findViewById(R.id.toolbar)

        navigationview = view.findViewById(R.id.navigation_view)

        val progressIndicator = view.findViewById<CircularProgressIndicator>(R.id.progress_circular)

        progressIndicator.isIndeterminate = true

        fragmentContainer.adapter = com.amp.nvamp.adapter.TabLayout(requireActivity())

        fragmentContainer.offscreenPageLimit = 6

        TabLayoutMediator(tabLayout, fragmentContainer) { tab, position ->
            tab.text = when (position) {
                0 -> "Songs"
                1 -> "Albums"
                2 -> "Folder"
                3 -> "Artist"
                4 -> "Gener"
                5 -> "Playlist"
                else -> "Unknown"
            }
        }.attach()

        // 🔗 Connect search bar with search view
        searchView.setupWithSearchBar(searchBar)


        // 🔍 Handle search text changes
        searchView.editText.addTextChangedListener { text ->

//            val query = text.toString().lowercase()
//
//            filteredSongs = if (query.isEmpty()) {
//                allSongs.toMutableList()
//                else{
//                    allSongs.filter { song ->
//                        song.title.lowercase().contains(query) || song.artist.lowercase()
//                            .contains(query)
//                    }.toMutableList()
//                }
//            }
//
//            adapter.submit
        }

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        // Handle menu item clicks
        navigationview.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settingsd -> { /* Handle Home */
                    val settingsintent = Intent(context, NvampPlayerSettings::class.java)
                    startActivity(settingsintent)
                }

                R.id.refreshd -> { /* Handle Settings */
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


                        Snackbar.make(
                            view,
                            "All Songs Refreshed from disk",
                            Snackbar.ANIMATION_MODE_FADE
                        )
                            .show()
                    }
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


//        toolbar.setOnMenuItemClickListener { menuItem ->
//            if (menuItem.title == "Refresh"){
//                lifecycleScope.launch {
//                    loaderview.visibility = VISIBLE
//                    MainActivity.playerViewModel.refreshdatainpref()
//                    HomeFragment.playernotify()
//                    MusicLibrary.playernotify()
//                    FolderFragment.playernotify()
//                    ArtistFragment.playernotify()
//                    GenerFragment.playernotify()
//                    PlaylistFragment.playernotify()
//                    loaderview.visibility = GONE
//
//
//                    Snackbar.make(view, "All Songs Refreshed from disk", Snackbar.ANIMATION_MODE_FADE)
//                        .show()
//
//                }
//                return@setOnMenuItemClickListener true
//            }else if (menuItem.title == "Settings"){
//                val settingsintent = Intent(context,NvampPlayerSettings::class.java)
//                startActivity(settingsintent)
//            }else if (menuItem.title == "Equalizer"){
//                val intent: Intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
//                    putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId)
//                    putExtra(AudioEffect.EXTRA_PACKAGE_NAME, requireContext().packageName)
//                    putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
//                }
//
//                requireContext().startActivity(intent)
//
//                return@setOnMenuItemClickListener true
//            }
//
//            return@setOnMenuItemClickListener false
//        }

    }
}