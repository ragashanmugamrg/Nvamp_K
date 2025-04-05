package com.amp.nvamp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.amp.nvamp.databinding.ActivityMainBinding
import com.amp.nvamp.playback.PlaybackService
import com.amp.nvamp.viewmodel.PlayerViewModel
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.mediaitems
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

        permissionRequest()

        playerViewModel = ViewModelProvider(this)[PlayerViewModel::class.java]

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            lifecycleScope.launch {
                PlayerViewModel(application).initialized()
            }
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




    private fun permissionRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Ask if was denied.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                    123
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    123
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PlayerViewModel(application).initialized()
            } else {
                Toast.makeText(this, "Permission denied. Cannot access media.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}