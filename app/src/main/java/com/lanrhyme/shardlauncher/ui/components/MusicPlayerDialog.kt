package com.lanrhyme.shardlauncher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.ui.components.SwitchLayout
import com.lanrhyme.shardlauncher.ui.components.TitleAndSummary
import com.lanrhyme.shardlauncher.ui.components.selectableCard
import dev.chrisbanes.haze.HazeState

@Composable
fun MusicPlayerDialog(
    onDismissRequest: () -> Unit,
    isCardBlurEnabled: Boolean,
    hazeState: HazeState
) {
    CustomDialog(onDismissRequest = onDismissRequest) {
        var selectedTab by remember { mutableStateOf(MusicPlayerTab.MusicList) }

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
                    IconButton(onClick = { onDismissRequest() }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Box(modifier = Modifier.weight(3f).padding(16.dp)) {
                    when (selectedTab) {
                        MusicPlayerTab.MusicList -> MusicListPage()
                        MusicPlayerTab.Settings -> MusicPlayerSettingsPage(isCardBlurEnabled, hazeState)
                    }
                }
            }
            CurrentlyPlayingCard()
        }
    }
}

enum class MusicPlayerTab {
    MusicList,
    Settings
}

@Composable
fun MusicListPage() {
    var showSelectDirectoryDialog by remember { mutableStateOf(false) }

    if (showSelectDirectoryDialog) {
        SelectDirectoryDialog(onDismissRequest = { showSelectDirectoryDialog = false })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var showPlayModeMenu by remember { mutableStateOf(false) }

            IconButton(onClick = { /* TODO: Search */ }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
            IconButton(onClick = { /* TODO: Refresh */ }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
            Box {
                IconButton(onClick = { showPlayModeMenu = true }) {
                    Icon(Icons.Default.Repeat, contentDescription = "Play Mode")
                }
                DropdownMenu(
                    expanded = showPlayModeMenu,
                    onDismissRequest = { showPlayModeMenu = false }
                ) {
                    DropdownMenuItem(text = { Text("单曲循环") }, onClick = { /* TODO */ })
                    DropdownMenuItem(text = { Text("顺序播放") }, onClick = { /* TODO */ })
                    DropdownMenuItem(text = { Text("随机播放") }, onClick = { /* TODO */ })
                }
            }
            IconButton(onClick = { /* TODO: Add Music */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Music")
            }
            IconButton(onClick = { showSelectDirectoryDialog = true }) {
                Icon(Icons.Default.FolderOpen, contentDescription = "Select Directory")
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            val musicItems = listOf( // Placeholder data
                MusicItem("Song 1", "Artist 1", ""),
                MusicItem("Song 2", "Artist 2", ""),
                MusicItem("Song 3", "Artist 3", ""),
            )
            items(musicItems) { item ->
                MusicCard(
                    item = item,
                    isSelected = false, // TODO: handle selection
                    onCLick = { /* TODO */ },
                    onDelete = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun SelectDirectoryDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("选择目录") },
        text = {
            // TODO: Implement directory selection UI
            Text("Directory selection placeholder")
        },
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text("确认")
            }
        }
    )
}

@Composable
fun MusicPlayerSettingsPage(
    isCardBlurEnabled: Boolean,
    hazeState: HazeState
) {
    var autoPlay by remember { mutableStateOf(true) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            SwitchLayout(
                checked = autoPlay,
                onCheckedChange = { autoPlay = !autoPlay },
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
    var showDeleteMenu by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .selectableCard(isSelected = isSelected, isPressed = isPressed)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onCLick() },
                    onLongPress = { showDeleteMenu = true }
                )
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = "Album Art")
            }

            Box(modifier = Modifier.padding(16.dp)) {
                TitleAndSummary(title = item.title, summary = item.artist)
            }

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

data class MusicItem(
    val title: String,
    val artist: String,
    val albumArtUri: String
)

@Composable
fun CurrentlyPlayingCard() {
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.3f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
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
                Icon(Icons.Default.MusicNote, contentDescription = "Album Art")
            }

            // Song Info and Progress
            Column(modifier = Modifier.weight(1f)) {
                Text("Song Title", style = MaterialTheme.typography.titleMedium)
                Slider(value = progress, onValueChange = { progress = it })
            }

            // Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* TODO: Previous */ }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                }
                IconButton(onClick = { isPlaying = !isPlaying }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }
                IconButton(onClick = { /* TODO: Next */ }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next")
                }
            }
        }
    }
}
