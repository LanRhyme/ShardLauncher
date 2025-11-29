package com.lanrhyme.shardlauncher.ui.music

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.lanrhyme.shardlauncher.data.MusicRepository
import com.lanrhyme.shardlauncher.data.SettingsRepository
import com.lanrhyme.shardlauncher.model.MusicItem
import com.lanrhyme.shardlauncher.service.MusicPlayerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicPlayerViewModel(
    application: Application,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {

    private val musicRepository = MusicRepository(application)

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val _mediaController = MutableStateFlow<MediaController?>(null)
    val mediaController: StateFlow<MediaController?> = _mediaController.asStateFlow()

    private val _musicList = MutableStateFlow<List<MusicItem>>(emptyList())
    private val _filteredMusicList = MutableStateFlow<List<MusicItem>>(emptyList())
    val musicList: StateFlow<List<MusicItem>> = _filteredMusicList.asStateFlow()

    private val _currentScanDirectory = MutableStateFlow<String?>(null)
    val currentScanDirectory: StateFlow<String?> = _currentScanDirectory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _repeatMode = MutableStateFlow<Int>(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    init {
        val sessionToken = SessionToken(application,
            android.content.ComponentName(application, MusicPlayerService::class.java))
        mediaControllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            _mediaController.value = mediaControllerFuture.get()
            _mediaController.value?.repeatMode = settingsRepository.getMusicRepeatMode() // Restore saved repeat mode
            _repeatMode.value = _mediaController.value?.repeatMode ?: Player.REPEAT_MODE_OFF

            _mediaController.value?.addListener(object : Player.Listener {
                override fun onRepeatModeChanged(repeatMode: Int) {
                    _repeatMode.value = repeatMode
                    settingsRepository.setMusicRepeatMode(repeatMode) // Save repeat mode
                }
            })
        }, MoreExecutors.directExecutor())

        _currentScanDirectory.value = settingsRepository.getLastSelectedMusicDirectory()

        viewModelScope.launch {
            _searchQuery.collect { query ->
                filterMusicList(query, _musicList.value)
            }
        }
        viewModelScope.launch {
            _currentScanDirectory.collect { directory ->
                // When directory changes, reload music files and then filter
                _musicList.value = musicRepository.getMusicFiles(directory)
                filterMusicList(_searchQuery.value, _musicList.value)
            }
        }
        viewModelScope.launch {
            _musicList.collect {
                // When _musicList changes (due to initial load or directory change), filter it
                filterMusicList(_searchQuery.value, it)
            }
        }
    }

    private fun filterMusicList(query: String, list: List<MusicItem>) {
        _filteredMusicList.value = list.filter {
            it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
        }
    }

    fun setMusicScanDirectory(directory: String) {
        _currentScanDirectory.value = directory
    }

    fun loadMusicFiles() {
        // This will trigger _currentScanDirectory.collect, which in turn reloads and filters
        viewModelScope.launch {
            _currentScanDirectory.value = _currentScanDirectory.value // Trigger collection
        }
    }

    fun searchMusic(query: String) {
        _searchQuery.value = query
    }

    fun addMusicFile(uri: android.net.Uri) {
        viewModelScope.launch {
            val musicItem = musicRepository.getMusicItemFromUri(uri)
            musicItem?.let {
                _musicList.value = _musicList.value + it
            }
        }
    }

    fun setRepeatMode(mode: Int) {
        mediaController.value?.repeatMode = mode
        settingsRepository.setMusicRepeatMode(mode)
    }

    fun deleteMusicItem(musicItem: MusicItem) {
        _musicList.value = _musicList.value.filter { it != musicItem }
    }

    override fun onCleared() {
        super.onCleared()
        MediaController.releaseFuture(mediaControllerFuture)
    }

    class Factory(
        private val application: Application,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MusicPlayerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MusicPlayerViewModel(application, settingsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
