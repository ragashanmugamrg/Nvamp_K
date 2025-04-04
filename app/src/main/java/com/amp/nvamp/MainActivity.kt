package com.amp.nvamp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentResolver
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.amp.nvamp.adapter.Songslistadapter
import com.amp.nvamp.adapter.TabLayout
import com.amp.nvamp.data.Album
import com.amp.nvamp.data.Song
import com.amp.nvamp.databinding.ActivityMainBinding
import com.amp.nvamp.fragments.HomeFragment
import com.amp.nvamp.playback.PlaybackService
import com.amp.nvamp.viewmodel.PlayerViewModel
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.mediaitems
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    var  binding: ActivityMainBinding? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val  browser: MediaBrowser?
        get() = if(browserFuture.isDone && !browserFuture.isCancelled) browserFuture.get() else null




    companion object{
        lateinit var medcontroller: ListenableFuture<MediaController>
        lateinit var customFragmentManager: FragmentManager
        private val handler: Handler = Handler(Looper.getMainLooper())

        lateinit var playerViewModel: PlayerViewModel
    }



    private lateinit var controller: MediaController



    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        enableEdgeToEdge()
        permissionrequest()

        playerViewModel = ViewModelProvider(this)[PlayerViewModel::class.java]

        lifecycleScope.launch {
            PlayerViewModel(application).initialized()
        }

        setContentView(R.layout.activity_main)



        customFragmentManager = supportFragmentManager


        medcontroller = MediaController.Builder(this,
            SessionToken(this,ComponentName(this,PlaybackService::class.java)),
            ).buildAsync()


        medcontroller.addListener(
            {
                if (medcontroller.isDone) {
                    controller = medcontroller.get()
                    controller.setMediaItems(mediaitems)
                }
            }, MoreExecutors.directExecutor()
        )

    }




    private fun permissionrequest(){

        var permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            permission = Manifest.permission.READ_MEDIA_AUDIO
        }

        if(ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED){

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){
            MaterialAlertDialogBuilder(this)
                                .setCancelable(false)
                .setTitle("Permission Request")
                .setNegativeButton("cancle", ((({ dialog, which -> dialog.dismiss() }))))
                .setPositiveButton("ok",({dialog,which ->
                    ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
                    dialog.dismiss()
                }))
                .show()

        }else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1);
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            permission = Manifest.permission.READ_MEDIA_AUDIO
        }

        if(requestCode >= 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else if(!ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){
                MaterialAlertDialogBuilder(this)
                    .setCancelable(false)
                    .setTitle("Permission Request")
                    .setNegativeButton("cancle", ((({ dialog, which -> dialog.dismiss() }))))
                    .setPositiveButton("ok",({dialog,which ->
                        ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
                        dialog.dismiss()
                    }))
                    .show()
            }else{
                permissionrequest()
            }
        }
    }
}