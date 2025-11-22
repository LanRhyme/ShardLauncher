package com.lanrhyme.shardlauncher.ui.version

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lanrhyme.shardlauncher.R
import com.lanrhyme.shardlauncher.ui.components.animatedAppearance
import com.lanrhyme.shardlauncher.ui.components.selectableCard

data class GameVersion(val name: String, val icon: Int)

enum class VersionDetailPane(val title: String, val icon: ImageVector) {
    Config("版本配置", Icons.Default.Settings),
    Mods("模组管理", Icons.Default.Extension),
    Saves("存档管理", Icons.Default.Save),
    ResourcePacks("资源包管理", Icons.Default.Style),
    ShaderPacks("光影包管理", Icons.Default.WbSunny)
}

@Composable
fun VersionScreen(navController: NavController, animationSpeed: Float) {
    var selectedVersion by remember { mutableStateOf<GameVersion?>(null) }
    var selectedPane by remember { mutableStateOf<VersionDetailPane?>(null) }

    fun resetToVersionList() {
        selectedPane = null
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Left Pane (25%)
        Card(
            modifier = Modifier
                .weight(0.25f)
                .fillMaxHeight()
                .padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 16.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            LeftNavigationPane(
                selectedVersion = selectedVersion,
                selectedPane = selectedPane,
                onPaneSelected = { pane -> selectedPane = pane }
            )
        }

        // Right Pane (75%)
        Box(modifier = Modifier
            .weight(0.75f)
            .fillMaxHeight()) {
            Crossfade(targetState = selectedPane, label = "RightPaneCrossfade") {
                pane ->
                if (pane == null) {
                    GameVersionListContent(
                        selectedVersion = selectedVersion,
                        onVersionClick = { version ->
                            selectedVersion = version
                        },
                        animationSpeed = animationSpeed
                    )
                } else {
                    RightDetailContent(pane, selectedVersion, onBack = { resetToVersionList() })
                }
            }
        }
    }
}

@Composable
fun LeftNavigationPane(
    selectedVersion: GameVersion?,
    selectedPane: VersionDetailPane?,
    onPaneSelected: (VersionDetailPane) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = selectedVersion != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = selectedVersion!!.icon),
                        contentDescription = selectedVersion.name,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp)
                    )
                    Text(
                        text = selectedVersion.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                }
        }

        if (selectedVersion == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("请从右侧选择一个版本", textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(VersionDetailPane.entries) {
                    pane ->
                    val isSelected = selectedPane == pane
                    TextButton(
                        onClick = { onPaneSelected(pane) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = pane.icon, contentDescription = pane.title)
                            Spacer(Modifier.width(16.dp))
                            Text(pane.title)
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun GameVersionListContent(selectedVersion: GameVersion?, onVersionClick: (GameVersion) -> Unit, animationSpeed: Float) {
    val versions = remember {
        listOf(
            GameVersion("1.8.9pvp", R.drawable.img_minecraft),
            GameVersion("1.12.2", R.drawable.img_minecraft),
            GameVersion("1.21.10fabric", R.drawable.img_minecraft),
            GameVersion("1.14.4", R.drawable.img_minecraft),
            GameVersion("1.13.2", R.drawable.img_minecraft),
            GameVersion("1.9.4forgeaaa", R.drawable.img_minecraft),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var searchText by remember { mutableStateOf("") }
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("搜索版本") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { /* TODO: Refresh */ }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
            IconButton(onClick = { /* TODO: Sort */ }) {
                Icon(Icons.Default.Sort, contentDescription = "Sort")
            }
            IconButton(onClick = { /* TODO: Filter */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Filter")
            }
            IconButton(onClick = { /* TODO: Directory switch */ }) {
                Icon(Icons.Default.Folder, contentDescription = "Directory")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(versions) { index, version ->
                GameVersionCard(
                    version = version,
                    isSelected = version == selectedVersion,
                    onClick = { onVersionClick(version) },
                    index = index,
                    animationSpeed = animationSpeed
                )
            }
        }
    }
}


@Composable
fun RightDetailContent(pane: VersionDetailPane, version: GameVersion?, onBack: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to version list")
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${pane.title} - ${version?.name ?: ""}",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(Modifier.height(16.dp))
        // Placeholder for the actual content of each pane
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("这是 ${pane.title} 页面", fontSize = 20.sp)
        }
    }
}

@Composable
fun GameVersionCard(version: GameVersion, isSelected: Boolean, onClick: () -> Unit, index: Int, animationSpeed: Float) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shape = RoundedCornerShape(18.dp)

    Card(
        modifier = Modifier
            .animatedAppearance(index, animationSpeed)
            .size(150.dp)
            .selectableCard(isSelected = isSelected, isPressed = isPressed)
            .clip(shape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = shape,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.5f)
            ) {
                Image(
                    painter = painterResource(id = version.icon),
                    contentDescription = "${version.name} icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = version.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }
    }
}
