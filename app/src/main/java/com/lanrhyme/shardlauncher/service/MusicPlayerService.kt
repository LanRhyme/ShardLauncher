package com.lanrhyme.shardlauncher.service

import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.lanrhyme.shardlauncher.data.MusicRepository
import com.lanrhyme.shardlauncher.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicPlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var musicRepository: MusicRepository
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    // Keep a reference to the player so we can update volume via onStartCommand
    private var player: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(applicationContext)
        musicRepository = MusicRepository(applicationContext)

        player = ExoPlayer.Builder(this).build().apply {
            // Apply saved music volume
            volume = settingsRepository.getMusicVolume()
        }
        mediaSession = MediaSession.Builder(this, player!!).build()

        if (settingsRepository.getAutoPlayMusic()) {
            serviceScope.launch {
                val lastSelectedDirectory = settingsRepository.getLastSelectedMusicDirectory()
                val musicFiles = musicRepository.getMusicFiles(lastSelectedDirectory)
                if (musicFiles.isNotEmpty()) {
                    val mediaItems = musicFiles.map { musicItem ->
                        MediaItem.Builder()
                            .setMediaId(musicItem.mediaUri)
                            .setUri(musicItem.mediaUri)
                            .setMediaMetadata(
                                androidx.media3.common.MediaMetadata.Builder()
                                    .setTitle(musicItem.title)
                                    .setArtist(musicItem.artist)
                                    .setArtworkUri(android.net.Uri.parse(musicItem.albumArtUri))
                                    .build()
                            )
                            .build()
                    }
                    player?.setMediaItems(mediaItems)
                    player?.prepare()
                    player?.playWhenReady = true
                    player?.repeatMode = settingsRepository.getMusicRepeatMode()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            if (action == ACTION_SET_VOLUME) {
                val vol = intent.getFloatExtra(EXTRA_VOLUME, settingsRepository.getMusicVolume())
                // Persist the volume in settings repository as well
                settingsRepository.setMusicVolume(vol)
                player?.let { p ->
                    p.volume = vol
                    android.util.Log.d("MusicPlayerService", "Volume set via intent: $vol")
                } ?: run {
                    android.util.Log.d("MusicPlayerService", "Received volume intent but player is null. Saved volume: $vol")
                }
                return START_NOT_STICKY
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player?.release()
            release()
            mediaSession = null
        }
        player = null
        super.onDestroy()
    }

    companion object {
        const val ACTION_SET_VOLUME = "com.lanrhyme.shardlauncher.action.SET_MUSIC_VOLUME"
        const val EXTRA_VOLUME = "com.lanrhyme.shardlauncher.extra.MUSIC_VOLUME"
    }
}
