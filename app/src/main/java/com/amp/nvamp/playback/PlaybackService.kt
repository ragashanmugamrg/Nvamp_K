package com.amp.nvamp.playback

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.os.Build
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
import com.amp.nvamp.MainActivity
import com.amp.nvamp.MainActivity.Companion.playerViewModel
import com.amp.nvamp.viewmodel.PlayerViewModel

class PlaybackService : MediaSessionService(), Player.Listener, AudioManager.OnAudioFocusChangeListener {

    private var mediaSession: MediaSession? = null
    private var equalizer: Equalizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    private var dynamicsProcessing: DynamicsProcessing? = null
    private var sessionId: Int = 0
    private var hasInitializedPlayback = false

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


        val sessionIntent = Intent(this.applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, sessionIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent)
            .build()


        setupAudioEffectsSafely()
    }

    @OptIn(UnstableApi::class)
    private fun setupAudioEffectsSafely() {
        player.addListener(object : Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                sessionId = audioSessionId
                Log.d("PlaybackService", "AudioSessionId ready: $sessionId")

                // Clean up any existing effects to prevent leaks or conflicts
                releaseAudioEffects()

                try {
                    // 🎚️ Equalizer — tuned for headphone clarity
                    equalizer = Equalizer(0, sessionId).apply {
                        enabled = true
                        val bandRange = bandLevelRange
                        val minLevel = bandRange[0]
                        val maxLevel = bandRange[1]
                        val numBands = numberOfBands

                        for (i in 0 until numBands) {
                            val centerFreq = getCenterFreq(i.toShort()) / 1000 // Hz
                            val level: Short = when {
                                centerFreq < 120 -> (maxLevel * 0.7f).toInt().toShort()   // Deep bass boost
                                centerFreq in 120..400 -> (maxLevel * 0.3f).toInt().toShort() // Warmth
                                centerFreq in 400..1000 -> (minLevel * 0.2f).toInt().toShort() // Clean mids
                                centerFreq in 1000..4000 -> (maxLevel * 0.4f).toInt().toShort() // Vocal clarity
                                else -> (maxLevel * 0.6f).toInt().toShort()           // Sparkle highs
                            }
                            setBandLevel(i.toShort(), level)
                        }
                    }

                    // 🔊 Loudness enhancer — push overall presence
                    loudnessEnhancer = LoudnessEnhancer(sessionId).apply {
                        setTargetGain(1800) // +18 dB — strong, but check for clipping
                        enabled = true
                    }

                    // 🧠 DynamicsProcessing — compression + bass/treble enhancement
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val config = DynamicsProcessing.Config.Builder(
                            DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                            1,  // channels
                            true, 1, true, 1,true,1,true
                        ).build()

                        val channel = dynamicsProcessing?.getChannelByChannelIndex(0)
                        channel?.getPreEqBand(0)?.gain = 3.0f
                        channel?.getPostEqBand(config.postEqBandCount - 1)?.gain = 2.0f

                        // configure limiter
                        channel?.limiter?.apply {
                            attackTime = 20f
                            releaseTime = 300f
                            ratio = 2.5f
                            threshold = -10f
                        }
                        Log.d("PlaybackService", "DynamicsProcessing applied for headphones")
                    }

                } catch (e: Exception) {
                    Log.e("PlaybackService", "AudioEffect error: ${e.stackTraceToString()}")
                }
            }
        })
    }

    /**
     * Safely release audio effects before re-creating them
     */
    private fun releaseAudioEffects() {
        try {
            equalizer?.release()
            loudnessEnhancer?.release()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                dynamicsProcessing?.release()
            }
        } catch (e: Exception) {
            Log.w("PlaybackService", "Error releasing audio effects: ${e.message}")
        } finally {
            equalizer = null
            loudnessEnhancer = null
            dynamicsProcessing = null
        }
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
        if (!hasInitializedPlayback) {
            hasInitializedPlayback = true
            return
        }
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
