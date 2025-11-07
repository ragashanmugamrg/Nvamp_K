package com.amp.nvamp.bottombar


import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.amp.nvamp.MainActivity
import com.amp.nvamp.MainActivity.Companion.playerViewModel
import com.amp.nvamp.MainActivity.Companion.ahandler
import com.amp.nvamp.NvampApplication
import com.amp.nvamp.R
import com.amp.nvamp.data.Song
import com.amp.nvamp.fragments.PlaylistFragment
import com.amp.nvamp.playback.PlaybackService
import com.amp.nvamp.utils.NvampUtils
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.mediaitems
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors


class PlayerBottomSheet(context: Context, attribute: AttributeSet) :
    ConstraintLayout(context, attribute) {

    lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>

    var main = MainActivity()

    var bottomnav: BottomNavigationView? = null

    var miniplayerview: ConstraintLayout

    var minplayerplaypause: MaterialButton
    var title: TextView
    var artist: TextView

    var minititle: TextView
    var miniartist: TextView

    var rightduration: TextView
    var leftduration: TextView

    var albumart: ImageView
    var minialbumart: ImageView

    var playpause: MaterialButton
    var slider: Slider

    var nextbutton: MaterialButton
    var previousbutton: MaterialButton

    var shuffleMode: MaterialButton
    var repeatMode: MaterialButton

    var appBar: MaterialToolbar


    lateinit var mediaController: ListenableFuture<MediaController>
    lateinit var controller: MediaController

    val playlistmap = mutableMapOf<String,List<Song>>()
    val dynamicChoice: Set<String> = playlistmap.keys

    init {
        inflate(context, R.layout.player_bottom_sheet, this)
        miniplayerview = findViewById(R.id.miniplayerview)
        minplayerplaypause = findViewById(R.id.miniplaypause)
        albumart = findViewById(R.id.imageView2)
        title = findViewById(R.id.titlesongssssss)
        artist = findViewById(R.id.artistsong)
        minititle = findViewById(R.id.dialogartist)
        miniartist = findViewById(R.id.dialogtitle)
        minialbumart = findViewById(R.id.imageView)

        playpause = findViewById(R.id.materialButton2)

        rightduration = findViewById(R.id.rightduration)
        slider = findViewById(R.id.playerseekbar)

        nextbutton = findViewById(R.id.materialnext)
        previousbutton = findViewById(R.id.materialButton3)

        shuffleMode = findViewById(R.id.materialButton)
        repeatMode = findViewById(R.id.materialButton4)

        leftduration = findViewById(R.id.leftduration)

        appBar = findViewById(R.id.topAppBar)

        playlistmap.putAll(playerViewModel.getplayListMusic())

        val handler = Handler(Looper.getMainLooper())

    }


    var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback = object :
        BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {

                BottomSheetBehavior.STATE_COLLAPSED -> miniplayerview.visibility = VISIBLE

                BottomSheetBehavior.STATE_EXPANDED -> miniplayerview.visibility = GONE

                BottomSheetBehavior.STATE_HALF_EXPANDED -> miniplayerview.visibility = GONE

                BottomSheetBehavior.STATE_DRAGGING -> miniplayerview.visibility = GONE

            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (slideOffset < 0) {
                miniplayerview.alpha = 1 - (-1 * slideOffset)
                return
            }
        }
    }

    var seekbarplayer = object : Runnable {
        override fun run() {
            if (controller.isPlaying) {
                slider.value = controller.currentPosition.toFloat()
            }
            ahandler.postDelayed(this, 500)
        }
    }


    var sliderTouchListener = object : Slider.OnSliderTouchListener {
        override fun onStartTrackingTouch(slider: Slider) {
        }

        override fun onStopTrackingTouch(slider: Slider) {
            if (controller.currentMediaItem != null) {
                controller.seekTo(((slider.value).toLong()))
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        bottomSheet = PlayerBottomSheetBehaviour.from(this)

        mediaController = MediaController.Builder(
            NvampApplication.context,
            SessionToken(
                NvampApplication.context,
                ComponentName(NvampApplication.context, PlaybackService::class.java)
            ),
        ).buildAsync()


        mediaController.addListener(
            {
                if (mediaController.isDone) {
                    controller = mediaController.get()
                    controller.setMediaItems(mediaitems, playerViewModel.getlastplayedpos(),0L)
                    controller.addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            onPlaybackStateChanged(controller.playbackState)
                            ahandler.postDelayed(seekbarplayer, 500)
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            super.onPlaybackStateChanged(playbackState)
                            updateplaying()
                        }

                        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                            super.onMediaMetadataChanged(mediaMetadata)
                            updatemetadata(mediaMetadata)
                        }

                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            super.onMediaItemTransition(mediaItem, reason)
                            slider.value = 0f
                            ahandler.postDelayed(seekbarplayer, 500)
                        }

                    })
                }
            }, MoreExecutors.directExecutor()
        )


        if (mediaController.isDone) {
            controller = mediaController.get()
        }

        bottomSheet.addBottomSheetCallback(bottomSheetCallback)

        miniplayerview.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.UNSPECIFIED
        )


        bottomSheet.setPeekHeight(miniplayerview.measuredHeight, false)


        minplayerplaypause.setOnClickListener {
            if (mediaController.isDone) {
                if (controller.isPlaying)
                    controller.pause()
                else if (!controller.isPlaying)
                    controller.play()
            }
        }


        playpause.setOnClickListener {
            if (mediaController.isDone) {
                if (controller.isPlaying)
                    controller.pause()
                else if (!controller.isPlaying)
                    controller.play()
                updateplaying()
            }
        }

        nextbutton.setOnClickListener {
            controller.seekToNext()
        }

        previousbutton.setOnClickListener {
            controller.seekToPrevious()
        }

        shuffleMode.setOnClickListener {
            when (controller.shuffleModeEnabled) {
                true -> {
                    shuffleMode.setIconResource(R.drawable.shuffle_24px)
                    controller.shuffleModeEnabled = false
                }


                false -> {
                    shuffleMode.setIconResource(R.drawable.shuffle_on_24px)
                    controller.shuffleModeEnabled = true
                }
            }
        }



        repeatMode.setOnClickListener {
            when (controller.repeatMode) {
                Player.REPEAT_MODE_OFF -> {
                    repeatMode.setIconResource(R.drawable.repeat_on_24px)
                    controller.repeatMode = Player.REPEAT_MODE_ALL
                }

                Player.REPEAT_MODE_ALL -> {
                    repeatMode.setIconResource(R.drawable.repeat_one_on_24px)
                    controller.repeatMode = Player.REPEAT_MODE_ONE
                }

                Player.REPEAT_MODE_ONE -> {
                    repeatMode.setIconResource(R.drawable.repeat_24px)
                    controller.repeatMode = Player.REPEAT_MODE_OFF
                }

            }
        }


        slider.addOnSliderTouchListener(sliderTouchListener)

        slider.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
            leftduration.text = NvampUtils().formatDuration(value.toLong())
        })

        appBar.setNavigationOnClickListener {
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }




        appBar.setOnMenuItemClickListener { item ->

            var playlistnames = mutableListOf<String>()
            dynamicChoice.forEach { name ->
                playlistnames.add(name)
            }

            if (item.title == "favorite") {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Save")
                    .setNeutralButton("ADD") { dialog, id ->
                        val editText = EditText(context)
                        MaterialAlertDialogBuilder(context)
                            .setTitle("playlist")
                            .setView(editText)
                            .setNegativeButton("cancle") { dialog1, which ->
                                dialog.dismiss()
                            }
                            .setPositiveButton("ok") { dialog, which ->
                                val playlistname = editText.text
                                val playlist = Song(
                                controller.mediaMetadata.title.toString(),
                                controller.mediaMetadata.artist.toString(),
                                controller.duration,
                                    controller.mediaMetadata.description.toString(),
                                "",
                                "",
                                0L,
                                controller.mediaMetadata.artworkUri!!,
                                "",
                                "",
                                    controller.mediaMetadata.description.toString(),
                                0,
                                    0,
                                    0L
                            )
                                var newplaylist = mutableListOf<Song>()
                                newplaylist.add(playlist)
                                playlistmap.put(playlistname.toString(),newplaylist)
                                playerViewModel.setplayListMusic(playlistmap)
                                PlaylistFragment.playernotify()
                            }
                            .show()
                    }
                    .setNegativeButton("cancle") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("ok",{ dialog: DialogInterface?, which: Int ->
                        PlaylistFragment.playernotify()
                        })
                    .setMultiChoiceItems(playlistnames.toTypedArray(),null,){ dialog,which,ischecked ->
                        val selectedItem = playlistnames[which]
                        if(ischecked) {
                            val playlist = Song(
                                controller.mediaMetadata.title.toString(),
                                controller.mediaMetadata.artist.toString(),
                                controller.duration,
                                controller.mediaMetadata.description.toString(),
                                "",
                                "",
                                0L,
                                controller.mediaMetadata.artworkUri!!,
                                "",
                                "",
                                controller.mediaMetadata.description.toString(),
                                0,
                                0,
                                0L
                            )
                            var newplaylist = mutableListOf<Song>()
                            val play = playlistmap.get(selectedItem)?.toMutableList()
                            if (play!=null){
                                play.forEach { data ->
                                    newplaylist.add(data)
                                }
                            }
                            newplaylist.add(playlist)
                            playlistmap.put(selectedItem,newplaylist)
                            playerViewModel.setplayListMusic(playlistmap)
                        }
                    }
                    .show()

                return@setOnMenuItemClickListener true
            } else {
                return@setOnMenuItemClickListener false
            }
        }

    }


    @OptIn(UnstableApi::class)
    fun updatemetadata(mediaMetadata: MediaMetadata) {
        title.text = mediaMetadata.title
        artist.text = mediaMetadata.artist
        minititle.text = mediaMetadata.title
        miniartist.text = mediaMetadata.artist

        rightduration.text = mediaMetadata.durationMs?.let { NvampUtils().formatDuration(it) }

        Glide.with(NvampApplication.context)
            .load(mediaMetadata.artworkUri)
            .placeholder(R.drawable.ic_songs_foreground)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(albumart)

        Glide.with(NvampApplication.context)
            .load(mediaMetadata.artworkUri)
            .placeholder(R.drawable.ic_songs_foreground)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(minialbumart)

        slider.valueTo = mediaMetadata.durationMs?.toFloat() ?: 0f

    }

    fun updateplaying() {
        if (controller.isPlaying) {
            minplayerplaypause.setIconResource(R.drawable.pause_40px)
            playpause.setIconResource(R.drawable.pause_40px)
        } else if (!controller.isPlaying) {
            minplayerplaypause.setIconResource(R.drawable.play_arrow_40px)
            playpause.setIconResource(R.drawable.play_arrow_40px)
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

}