package com.amp.nvamp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import com.amp.nvamp.databinding.ActivityMainBinding
import com.amp.nvamp.fragments.ArtistFragment
import com.amp.nvamp.fragments.FolderFragment
import com.amp.nvamp.fragments.GenerFragment
import com.amp.nvamp.fragments.HomeFragment
import com.amp.nvamp.fragments.MusicLibrary
import com.amp.nvamp.fragments.ViewPagerFragment.Companion.loaderview
import com.amp.nvamp.utils.NvampUtils
import com.amp.nvamp.viewmodel.PlayerViewModel
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.mediaitems
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private var isRestored = false
    private val browser: MediaBrowser?
        get() = if (browserFuture.isDone && !browserFuture.isCancelled) browserFuture.get() else null

    companion object {
        lateinit var customFragmentManager: FragmentManager
        val ahandler: Handler = Handler(Looper.getMainLooper())
        lateinit var playerViewModel: PlayerViewModel
    }

    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionRequest()

        playerViewModel = ViewModelProvider(this)[PlayerViewModel::class.java]

        playerViewModel.controllerFuture.addListener({
            val controller = playerViewModel.controller
            prepareController(controller)
        }, MoreExecutors.directExecutor())

        lifecycleScope.launch {
            PlayerViewModel(application).initialized()
            HomeFragment.playernotify()
            MusicLibrary.playernotify()
            FolderFragment.playernotify()
            ArtistFragment.playernotify()
            GenerFragment.playernotify()
        }

        setContentView(R.layout.activity_main)

        customFragmentManager = supportFragmentManager
    }

    private fun prepareController(controller: MediaController) {
        if (controller.isConnected) {
            val songmediaItems =
                playerViewModel.getlastPlayedMediaItem().map { data ->
                    NvampUtils().changeSongmodeltoMediaitem(data).build()
                }.toMutableList()

            controller.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (!isRestored && state == Player.STATE_READY) {
                            val lastIndex = playerViewModel.getlastplayedpos()
                            val lastMs = playerViewModel.getLastPlayedms().toLong()

                            controller.seekTo(lastIndex, lastMs)

                            isRestored = true
                        }
                    }

                    override fun onEvents(
                        player: Player,
                        events: Player.Events,
                    ) {
                        super.onEvents(player, events)

                        if (isRestored) {
                            playerViewModel.setlastplayedpos(player.currentMediaItemIndex)
                            playerViewModel.setLastPlayedms(player.currentPosition.toFloat())
                        }
                    }
                },
            )

            controller.setMediaItems(
                songmediaItems.ifEmpty { mediaitems },
            )
            controller.prepare()
        }
    }

    private fun permissionRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_AUDIO,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                    123,
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    123,
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        coroutineScope.launch {
            loaderview.visibility = VISIBLE
            PlayerViewModel(application).initialized()
            HomeFragment.playernotify()
            MusicLibrary.playernotify()
            FolderFragment.playernotify()
            ArtistFragment.playernotify()
            GenerFragment.playernotify()
            loaderview.visibility = GONE
        }
    }
}
