package com.amp.nvamp.bottombar

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.media.session.PlaybackState
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.amp.nvamp.MainActivity
import com.amp.nvamp.MainActivity.Companion.mediaitems
import com.amp.nvamp.NvampApplication
import com.amp.nvamp.R
import com.amp.nvamp.playback.PlaybackService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class PlayerBottomSheet(context:Context,attribute:AttributeSet): ConstraintLayout(context,attribute) {

    lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>

    var main = MainActivity()

    var bottomnav: BottomNavigationView? = null

    var miniplayerview: ConstraintLayout

    var minplayerplaypause: MaterialButton
    var albumart: ImageView


    lateinit var mediaController: ListenableFuture<MediaController>
    lateinit var controller: MediaController



    init {
        inflate(context, R.layout.player_bottom_sheet,this)
        miniplayerview = findViewById(R.id.miniplayerview)
        minplayerplaypause = findViewById(R.id.materialButton2)
        albumart = findViewById(R.id.imageView2)
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        bottomSheet = PlayerBottomSheetBehaviour.from(this)

        mediaController = MediaController.Builder(NvampApplication.context,
            SessionToken(NvampApplication.context, ComponentName(NvampApplication.context, PlaybackService::class.java)),
        ).buildAsync()


        mediaController.addListener(
            {
                if (mediaController.isDone) {
                    controller = mediaController.get()
                    controller.setMediaItems(mediaitems)
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
                albumart.setImageURI(controller.mediaMetadata.artworkUri)
                if (controller.playbackState == PlaybackState.STATE_PLAYING)
                    controller.pause()
                else if (controller.playbackState == PlaybackState.STATE_PAUSED)
                    controller.play()
            }
        }

    }





    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

}