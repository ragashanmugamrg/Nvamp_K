package com.amp.nvamp.playback

import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.session.PlaybackState
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.WakeMode
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.amp.nvamp.NvampApplication
import com.amp.nvamp.R
import com.google.common.util.concurrent.ListenableFuture

class PlaybackService : MediaSessionService(),Player.Listener,AudioManager.OnAudioFocusChangeListener {

    private var mediaSession: MediaSession? = null

    companion object{
        lateinit var player: ExoPlayer
        var sessionId: Int = 0
    }


    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(25000, 50000, 1000, 2000)
            .build()


        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .setAudioAttributes(audioAttributes,true)
            .build()

        player.addListener(this)
        player.setWakeMode(C.WAKE_MODE_LOCAL)
        sessionId = player.audioSessionId
        mediaSession = MediaSession.Builder(this, player).build()


        val sessionId = player.audioSessionId
        val loudnessEnhancer = LoudnessEnhancer(sessionId)
        loudnessEnhancer.setTargetGain(1000) // Gain in millibels
        loudnessEnhancer.enabled = true

        val equalizer = Equalizer(0, player.audioSessionId)
        equalizer.enabled = true



    }


    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)
        Log.d("Service","Track Listerner")

    }


    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }


    fun successfullyRetrievedAudioFocus(): Boolean {
        val audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var audioAttributes =  android.media.AudioAttributes.Builder()
            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        var audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setOnAudioFocusChangeListener(this)
            .build()
        val result = audioManager.requestAudioFocus(audioFocusRequest)
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }




    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onAudioFocusChange(position: Int) {
        when(position){
            AudioManager.AUDIOFOCUS_LOSS -> player.pause()
            AudioManager.AUDIOFOCUS_GAIN -> {
                player.volume = 1.0f
                player.play()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> player.pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> player.volume = 1.0f
        }
    }
}