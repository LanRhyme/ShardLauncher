package com.lanrhyme.shardlauncher.ui.downloads

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.R
import com.lanrhyme.shardlauncher.ui.components.CombinedCard
import com.lanrhyme.shardlauncher.ui.components.StyledFilterChip

enum class VersionType(val title: String) {
    Release("正式版"),
    Snapshot("快照版"),
    Ancient("远古版")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GameDownloadContent() {
    var selectedVersionTypes by remember { mutableStateOf(setOf(VersionType.Release)) }
    var searchQuery by remember { mutableStateOf("") }
    val versions = listOf("1.21", "1.20.4", "1.20.1", "1.19.4", "1.18.2", "1.17.1", "1.16.5") // Placeholder

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            CombinedCard(title = "版本筛选", summary = null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VersionType.entries.forEach { versionType ->
                        StyledFilterChip(
                            selected = versionType in selectedVersionTypes,
                            onClick = {
                                selectedVersionTypes = if (versionType in selectedVersionTypes) {
                                    selectedVersionTypes - versionType
                                } else {
                                    selectedVersionTypes + versionType
                                }
                            },
                            label = { Text(versionType.title) },
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Row(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        RoundedCornerShape(22.dp)
                                    )
                                    .padding(horizontal = 8.dp)
                                    .fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            "搜索",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        }
                    )
                    IconButton(
                        onClick = { /* TODO: Refresh logic */ },
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            }
        }

        items(versions.filter { it.contains(searchQuery, ignoreCase = true) }) { version ->
            VersionItem(version = version)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.VersionItem(version: String) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }

    val scale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.9f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_minecraft),
                contentDescription = "Minecraft",
                modifier = Modifier.size(32.dp)
            )
            Text(text = "Minecraft $version", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
