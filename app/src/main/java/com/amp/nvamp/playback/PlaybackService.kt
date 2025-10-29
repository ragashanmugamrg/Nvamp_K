package com.amp.nvamp.playback

import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.amp.nvamp.MainActivity.Companion.playerViewModel

class PlaybackService : MediaSessionService(), Player.Listener, AudioManager.OnAudioFocusChangeListener {

    private var mediaSession: MediaSession? = null
    private var equalizer: Equalizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    companion object {
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
            .setAudioAttributes(audioAttributes, true)
            .build()

        player.addListener(this)
        player.setWakeMode(C.WAKE_MODE_LOCAL)

        mediaSession = MediaSession.Builder(this, player).build()

        setupAudioEffectsSafely()
    }

    @OptIn(UnstableApi::class)
    private fun setupAudioEffectsSafely() {
        player.addListener(object : Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                sessionId = audioSessionId
                Log.d("PlaybackService", "AudioSessionId ready: $sessionId")

                try {
                    // Loudness Enhancer
                    loudnessEnhancer = LoudnessEnhancer(sessionId).apply {
                        setTargetGain(500) // Softer gain
                        enabled = true
                    }

                    // Equalizer
                    equalizer = Equalizer(0, sessionId)
                    equalizer?.enabled = true

                    val numBands = equalizer?.numberOfBands ?: 0
                    val bandRange = equalizer?.bandLevelRange
                    val minLevel = bandRange?.get(0) ?: -1500
                    val maxLevel = bandRange?.get(1) ?: 1500

                    Log.d("PlaybackService", "Equalizer bands: $numBands, level range: $minLevel to $maxLevel")

                    for (i in 0 until numBands) {
                        val level: Short = when (i.toInt()) {
                            0 -> (maxLevel * 0.5f).toInt().toShort()       // Boost bass
                            1 -> (maxLevel * 0.3f).toInt().toShort()       // Mid boost
                            (numBands - 1).toInt() -> (minLevel * 0.5f).toInt().toShort() // Reduce treble
                            else -> 0
                        }

                        if (level in minLevel..maxLevel) {
                            equalizer?.setBandLevel(i.toShort(), level)
                        }
                    }

                } catch (e: Exception) {
                    Log.e("PlaybackService", "AudioEffect error: ${e.message}")
                }
            }
        })
    }

    override fun onDestroy() {
        try {
            equalizer?.release()
            loudnessEnhancer?.release()
        } catch (_: Exception) {}

        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)
        Log.d("Service", "Track Listener")
        val lastplay = playerViewModel.getlastplayedpos()
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        val lastplay = playerViewModel.getlastplayedpos()
        mediaItem?.mediaMetadata?.trackNumber?.let { playerViewModel.setlastplayedpos(it) }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    fun successfullyRetrievedAudioFocus(): Boolean {
        val audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioAttributes = android.media.AudioAttributes.Builder()
            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setOnAudioFocusChangeListener(this)
            .build()

        val result = audioManager.requestAudioFocus(audioFocusRequest)
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
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
