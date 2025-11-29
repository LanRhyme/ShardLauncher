package com.lanrhyme.shardlauncher.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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

    // The user-visible name of our component.
    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(applicationContext)
        musicRepository = MusicRepository(applicationContext)

        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()

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
                    player.setMediaItems(mediaItems)
                    player.prepare()
                    player.playWhenReady = true
                    player.repeatMode = settingsRepository.getMusicRepeatMode()
                }
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
