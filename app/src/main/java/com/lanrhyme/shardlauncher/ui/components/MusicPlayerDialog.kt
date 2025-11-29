package com.lanrhyme.shardlauncher.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.lanrhyme.shardlauncher.data.MusicRepository
import com.lanrhyme.shardlauncher.data.SettingsRepository
import com.lanrhyme.shardlauncher.model.MusicItem
import com.lanrhyme.shardlauncher.ui.music.MusicPlayerViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MusicPlayerDialog(
    onDismissRequest: () -> Unit,
    isCardBlurEnabled: Boolean,
    hazeState: HazeState,
    musicPlayerViewModel: MusicPlayerViewModel
) {
    var selectedTab by remember { mutableStateOf(MusicPlayerTab.MusicList) }

    CustomDialog(onDismissRequest = onDismissRequest) {
        Column {
            Row(modifier = Modifier.weight(1f)) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationRailItem(
                        selected = selectedTab == MusicPlayerTab.MusicList,
                        onClick = { selectedTab = MusicPlayerTab.MusicList },
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Music List") },
                        label = { Text("音乐列表") }
                    )
                    NavigationRailItem(
                        selected = selectedTab == MusicPlayerTab.Settings,
                        onClick = { selectedTab = MusicPlayerTab.Settings },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("设置") }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        onDismissRequest()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Box(modifier = Modifier.weight(3f).padding(16.dp)) {
                    when (selectedTab) {
                        MusicPlayerTab.MusicList -> MusicListPage(musicPlayerViewModel = musicPlayerViewModel)
                        MusicPlayerTab.Settings -> MusicPlayerSettingsPage(
                            isCardBlurEnabled = isCardBlurEnabled,
                            hazeState = hazeState
                        )
                    }
                }
            }
            CurrentlyPlayingCard(musicPlayerViewModel = musicPlayerViewModel)
        }
    }
}

enum class MusicPlayerTab {
    MusicList,
    Settings
}

@Composable
fun MusicListPage(musicPlayerViewModel: MusicPlayerViewModel) {
    var showSelectDirectoryDialog by remember { mutableStateOf(false) }

    val musicList by musicPlayerViewModel.musicList.collectAsState()
    val mediaController by musicPlayerViewModel.mediaController.collectAsState()
    var currentMediaItem by remember { mutableStateOf(mediaController?.currentMediaItem) }

    DisposableEffect(mediaController) {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentMediaItem = mediaItem
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    currentMediaItem = mediaController?.currentMediaItem
                }
            }
        }
        mediaController?.addListener(listener)

        onDispose {
            mediaController?.removeListener(listener)
        }
    }

    val pickAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                musicPlayerViewModel.addMusicFile(it)
            }
        }
    )

    if (showSelectDirectoryDialog) {
        SelectDirectoryDialog(
            onDismissRequest = { showSelectDirectoryDialog = false },
            musicPlayerViewModel = musicPlayerViewModel
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val searchQuery by musicPlayerViewModel.searchQuery.collectAsState()

        // Top action bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var showPlayModeMenu by remember { mutableStateOf(false) }
            val repeatMode by musicPlayerViewModel.repeatMode.collectAsState()

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { musicPlayerViewModel.searchMusic(it) },
                label = { Text("搜索音乐") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { musicPlayerViewModel.loadMusicFiles() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
            Box {
                IconButton(onClick = { showPlayModeMenu = true }) {
                    Icon(
                        imageVector = when (repeatMode) {
                            Player.REPEAT_MODE_OFF -> Icons.Default.Repeat
                            Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                            Player.REPEAT_MODE_ALL -> Icons.Default.Shuffle
                            else -> Icons.Default.Repeat
                        },
                        contentDescription = "Play Mode"
                    )
                }
                DropdownMenu(
                    expanded = showPlayModeMenu,
                    onDismissRequest = { showPlayModeMenu = false }
                ) {
                    DropdownMenuItem(text = { Text("单曲循环") }, onClick = {
                        musicPlayerViewModel.setRepeatMode(Player.REPEAT_MODE_ONE)
                        showPlayModeMenu = false
                    })
                    DropdownMenuItem(text = { Text("顺序播放") }, onClick = {
                        musicPlayerViewModel.setRepeatMode(Player.REPEAT_MODE_OFF)
                        showPlayModeMenu = false
                    })
                    DropdownMenuItem(text = { Text("随机播放") }, onClick = {
                        musicPlayerViewModel.setRepeatMode(Player.REPEAT_MODE_ALL)
                        showPlayModeMenu = false
                    })
                }
            }
            IconButton(onClick = { pickAudioLauncher.launch("audio/*") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Music")
            }
            IconButton(onClick = { showSelectDirectoryDialog = true }) {
                Icon(Icons.Default.FolderOpen, contentDescription = "Select Directory")
            }
        }
        // Music list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(musicList) { item ->
                MusicCard(
                    item = item,
                    isSelected = item.mediaUri == currentMediaItem?.mediaId,
                    onCLick = {
                        val mediaItems = musicList.map { musicItem ->
                            MediaItem.Builder()
                                .setMediaId(musicItem.mediaUri)
                                .setUri(Uri.parse(musicItem.mediaUri))
                                .setMediaMetadata(
                                    MediaMetadata.Builder()
                                        .setTitle(musicItem.title)
                                        .setArtist(musicItem.artist)
                                        .setArtworkUri(Uri.parse(musicItem.albumArtUri))
                                        .build()
                                )
                                .build()
                        }
                        val selectedIndex = musicList.indexOf(item)
                        if (selectedIndex != -1) {
                            mediaController?.setMediaItems(mediaItems, selectedIndex, 0)
                            mediaController?.prepare()
                            mediaController?.play()
                        }
                    },
                    onDelete = { musicPlayerViewModel.deleteMusicItem(item) }
                )
            }
        }
    }
}

@Composable
fun SelectDirectoryDialog(
    onDismissRequest: () -> Unit,
    musicPlayerViewModel: MusicPlayerViewModel
) {
    val context = LocalContext.current
    val musicRepository = remember { MusicRepository(context) }
    val settingsRepository = remember { SettingsRepository(context) }
    var directories by remember { mutableStateOf(emptyList<String>()) }
    var tempSelectedDirectory by remember { mutableStateOf<String?>(null) } // Use temp state
    var showAddCustomDirectoryDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val savedDirectories = settingsRepository.getMusicDirectories()
        directories = (listOf("内置目录") + savedDirectories).distinct()
        tempSelectedDirectory = settingsRepository.getLastSelectedMusicDirectory() ?: directories.firstOrNull()
    }

    if (showAddCustomDirectoryDialog) {
        AddCustomDirectoryDialog(
            onDismissRequest = { showAddCustomDirectoryDialog = false },
            onAddDirectory = { newPath ->
                val newDirectories = (directories + newPath).distinct()
                directories = newDirectories
                tempSelectedDirectory = newPath
                scope.launch {
                    settingsRepository.setMusicDirectories(newDirectories.filter { it != "内置目录" })
                }
                showAddCustomDirectoryDialog = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("选择音乐目录") },
        text = {
            Column {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(directories) { directory ->
                        OutlinedCard( // Using OutlinedCard for better visual
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { tempSelectedDirectory = directory },
                            colors = CardDefaults.cardColors(
                                containerColor = if (tempSelectedDirectory == directory) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(1.dp, if (tempSelectedDirectory == directory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = directory.substringAfterLast('/'), style = MaterialTheme.typography.titleMedium)
                                Text(text = directory, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        scope.launch {
                            directories = listOf("内置目录") + musicRepository.getMusicDirectories()
                        }
                    }) {
                        Text("搜索")
                    }
                    Button(onClick = { showAddCustomDirectoryDialog = true }) {
                        Text("添加目录")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                tempSelectedDirectory?.let {
                    scope.launch {
                        settingsRepository.setLastSelectedMusicDirectory(it)
                    }
                    musicPlayerViewModel.setMusicScanDirectory(it)
                    musicPlayerViewModel.loadMusicFiles()
                }
                onDismissRequest()
            }) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("取消")
            }
        }
    )
}

@Composable
fun AddCustomDirectoryDialog(
    onDismissRequest: () -> Unit,
    onAddDirectory: (String) -> Unit
) {
    var newDirectoryPath by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("添加自定义目录") },
        text = {
            OutlinedTextField(
                value = newDirectoryPath,
                onValueChange = { newDirectoryPath = it },
                label = { Text("目录路径") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = {
                if (newDirectoryPath.isNotBlank()) {
                    onAddDirectory(newDirectoryPath)
                }
            }) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("取消")
            }
        }
    )
}

@Composable
fun MusicPlayerSettingsPage(
    isCardBlurEnabled: Boolean,
    hazeState: HazeState
) {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository(context) }
    var autoPlay by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(settingsRepository) {
        autoPlay = settingsRepository.getAutoPlayMusic()
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            SwitchLayout(
                checked = autoPlay,
                onCheckedChange = {
                    val newCheckedState = !autoPlay
                    autoPlay = newCheckedState
                    scope.launch {
                        settingsRepository.setAutoPlayMusic(newCheckedState)
                    }
                 },
                title = "启动启动器时自动播放",
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicCard(
    item: MusicItem,
    isSelected: Boolean,
    onCLick: () -> Unit,
    onDelete: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var showDeleteMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .pointerInput(Unit) { detectTapGestures(onLongPress = { showDeleteMenu = true }) }
            .clickable(onClick = onCLick)
            .selectableCard(isSelected, isPressed)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onCLick,
                onLongClick = { showDeleteMenu = true }
            ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Album Art
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.albumArtUri,
                    contentDescription = "Album Art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(Icons.Default.LibraryMusic)
                )
            }

            // Title and Summary
            Box(modifier = Modifier.padding(16.dp)) {
                TitleAndSummary(title = item.title, summary = item.artist)
            }

            // Delete Menu
            DropdownMenu(
                expanded = showDeleteMenu,
                onDismissRequest = { showDeleteMenu = false }
            ) {
                DropdownMenuItem(text = { Text("删除") }, onClick = {
                    onDelete()
                    showDeleteMenu = false
                })
            }
        }
    }
}

@Composable
fun CurrentlyPlayingCard(musicPlayerViewModel: MusicPlayerViewModel) {
    val mediaController by musicPlayerViewModel.mediaController.collectAsState()
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentMediaItem by remember { mutableStateOf<MediaItem?>(null) }

    LaunchedEffect(mediaController) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentMediaItem = mediaItem
                duration = mediaController?.duration ?: 0L
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    duration = mediaController?.duration ?: 0L
                    currentMediaItem = mediaController?.currentMediaItem
                    isPlaying = mediaController?.isPlaying == true
                }
            }
        }
        mediaController?.addListener(listener)

        // Initial state
        duration = mediaController?.duration ?: 0L
        currentMediaItem = mediaController?.currentMediaItem
        isPlaying = mediaController?.isPlaying == true

        // Coroutine to update position
        while (true) {
            currentPosition = mediaController?.currentPosition ?: 0L
            delay(1000) // Update every second
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Album Art
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = currentMediaItem?.mediaMetadata?.artworkUri,
                    contentDescription = "Album Art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(Icons.Default.LibraryMusic)
                )
            }

            // Song Info and Progress
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    currentMediaItem?.mediaMetadata?.title?.toString() ?: "No song playing",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                    onValueChange = {
                        mediaController?.seekTo((it * duration).toLong())
                    },
                    enabled = mediaController != null && duration > 0
                )
            }

            // Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { mediaController?.seekToPreviousMediaItem() },
                    enabled = mediaController?.hasPreviousMediaItem() == true
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                }
                IconButton(
                    onClick = {
                        if (isPlaying) {
                            mediaController?.pause()
                        } else {
                            mediaController?.play()
                        }
                    },
                    enabled = mediaController != null
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }
                IconButton(
                    onClick = { mediaController?.seekToNextMediaItem() },
                    enabled = mediaController?.hasNextMediaItem() == true
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next")
                }
            }
        }
    }
}
