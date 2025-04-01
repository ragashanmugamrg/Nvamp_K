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
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.amp.nvamp.adapter.Songslistadapter
import com.amp.nvamp.adapter.TabLayout
import com.amp.nvamp.databinding.ActivityMainBinding
import com.amp.nvamp.fragments.HomeFragment
import com.amp.nvamp.playback.PlaybackService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch


data class Song(
    val title: String,
    val artist: String,
    val duration: String?,
    val data: String,
    val album: String,
    val foldername: String,
    val album_id: Long,
    val imgUri: Uri
)


data class Album(
    val title: String,
    val artist: String,
    val cover: Drawable?,
    val songList: List<Song>
)

class MainActivity : AppCompatActivity() {

    var  binding: ActivityMainBinding? = null
    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val  browser: MediaBrowser?
        get() = if(browserFuture.isDone && !browserFuture.isCancelled) browserFuture.get() else null

    private val treePathStack: ArrayDeque<MediaItem> = ArrayDeque()

    var libraryListView: RecyclerView? = null

    var bottomnav: BottomNavigationView? = null



    companion object{
        var mediaitems = mutableListOf<MediaItem>()
        lateinit var medcontroller: ListenableFuture<MediaController>
        lateinit var deviceMusicByAlbum: Map<String, List<Song>>
        lateinit var deviceMusicByFolder: Map<String, List<Song>>
        lateinit var customFragmentManager: FragmentManager
    }


    lateinit var adapter: Songslistadapter
    private lateinit var controller: MediaController



    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permissionrequest()

        lifecycleScope.launch {
            dataIniziser()
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

    private fun dataIniziser() {
        val contentResolver: ContentResolver = this.contentResolver
        val songs = mutableListOf<Song>()

        val album = mutableListOf<Album>()


        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DISPLAY_NAME
        )
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val data =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val album =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val album_id =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val duration =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val display_name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val foldername = data.replace(display_name, "")

                val artworkUri = Uri.parse("content://media/external/audio/albumart")
                val imgUri = ContentUris.withAppendedId(
                    artworkUri,
                    album_id
                )

                val song = Song(title,artist,duration,data,album,foldername,album_id,imgUri)
                songs.add(song)

                val mediaItem = MediaItem.Builder().setMediaId(data)
                    .setUri(Uri.parse(data))
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(title)
                            .setArtist(artist)
                            .setArtworkUri(imgUri)
                            .build()
                    )

                mediaitems.add(mediaItem.build())
            }
        }

        deviceMusicByAlbum = songs
            .groupBy { it.album }

        deviceMusicByFolder = songs.groupBy {
            it.foldername
        }


    }



    private fun pushpath(root: MediaItem) {
        treePathStack.add(root)
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