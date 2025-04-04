package com.amp.nvamp.bottombar

import android.content.ComponentName
import android.content.Context
import android.media.metrics.PlaybackStateEvent
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.alpha
import androidx.core.view.ViewCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.session.legacy.PlaybackStateCompat
import com.amp.nvamp.MainActivity
import com.amp.nvamp.NvampApplication
import com.amp.nvamp.R
import com.amp.nvamp.playback.PlaybackService
import com.amp.nvamp.utils.NvampUtils
import com.amp.nvamp.viewmodel.PlayerViewModel.Companion.mediaitems
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class PlayerBottomSheet(context:Context,attribute:AttributeSet): ConstraintLayout(context,attribute) {

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
    var minialbumart : ImageView

    var playpause: MaterialButton
    var slider: Slider

    var nextbutton: MaterialButton
    var previousbutton: MaterialButton

    var shuffleMode: MaterialButton
    var repeatMode: MaterialButton


    lateinit var mediaController: ListenableFuture<MediaController>
    lateinit var controller: MediaController



    init {
        inflate(context, R.layout.player_bottom_sheet,this)
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



    }



    var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback = object :
        BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when(newState){

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
            if(controller.isPlaying){
                slider.value = controller.currentPosition.toFloat()
            }
            handler.postDelayed(this,500)
        }
    }



    var sliderTouchListener = object :Slider.OnSliderTouchListener{
        override fun onStartTrackingTouch(slider: Slider) {
            print(slider.value)
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
                    controller.setMediaItems(mediaitems)
                    controller.addListener(object : Player.Listener{
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            onPlaybackStateChanged(controller.playbackState)
                            handler.postDelayed(seekbarplayer,500)
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
                            println("hello")
                            slider.value = 0f
                            handler.postDelayed(seekbarplayer,500)
                        }

                    })
                }
            }, MoreExecutors.directExecutor()
        )


        if (mediaController.isDone){
            controller = mediaController.get()
        }

        bottomSheet.addBottomSheetCallback(bottomSheetCallback)

        miniplayerview.measure(
            MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),
            MeasureSpec.UNSPECIFIED
        )


        bottomSheet.setPeekHeight(miniplayerview.measuredHeight,false)


        minplayerplaypause.setOnClickListener{
            if (mediaController.isDone){
                if (controller.isPlaying)
                    controller.pause()

                else if (!controller.isPlaying)
                    controller.play()
            }
        }


        playpause.setOnClickListener{
            if (mediaController.isDone){
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
            controller.shuffleModeEnabled = true
            shuffleMode.setIconResource(R.drawable.shuffle_on_24px)
        }

        repeatMode.setOnClickListener {
            when(controller.repeatMode){
                Player.REPEAT_MODE_ONE ->
                    println("")
                Player.REPEAT_MODE_ALL ->
                    println("")
                Player.REPEAT_MODE_OFF ->
                    println("")
            }
        }


        slider.addOnSliderTouchListener(sliderTouchListener)

        slider.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
            leftduration.text = NvampUtils().formatDuration(value.toLong())
        })

    }


    @OptIn(UnstableApi::class)
    fun updatemetadata(mediaMetadata: MediaMetadata){
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

    fun updateplaying(){
        if(controller.isPlaying){
            minplayerplaypause.setIconResource(R.drawable.pause_40px)
            playpause.setIconResource(R.drawable.pause_40px)
        }else if(!controller.isPlaying){
            minplayerplaypause.setIconResource(R.drawable.play_arrow_40px)
            playpause.setIconResource(R.drawable.play_arrow_40px)
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

}