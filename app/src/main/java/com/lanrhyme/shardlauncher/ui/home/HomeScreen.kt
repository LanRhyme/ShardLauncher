package com.lanrhyme.shardlauncher.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lanrhyme.shardlauncher.R
import com.lanrhyme.shardlauncher.api.ApiClient
import com.lanrhyme.shardlauncher.data.SettingsRepository
import com.lanrhyme.shardlauncher.model.LatestVersionsResponse
import com.lanrhyme.shardlauncher.model.VersionInfo
import com.lanrhyme.shardlauncher.ui.components.CombinedCard
import com.lanrhyme.shardlauncher.ui.components.ScalingActionButton
import com.lanrhyme.shardlauncher.ui.components.glow
import com.lanrhyme.shardlauncher.ui.custom.XamlRenderer
import com.lanrhyme.shardlauncher.ui.custom.parseXaml
import com.lanrhyme.shardlauncher.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(enableVersionCheck: Boolean) {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository(context) }
    val isGlowEffectEnabled = remember { settingsRepository.getIsGlowEffectEnabled() }
    val xamlContent = remember { loadXaml(context, "home.xaml") }
    val nodes = parseXaml(xamlContent)
    var latestVersions by remember { mutableStateOf<LatestVersionsResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (enableVersionCheck) {
        LaunchedEffect(Unit) {
            Logger.log(context, "HomeScreen", "Version check enabled. Starting polling.")
            var backoffDelay = 60 * 1000L // 1 minute initial backoff
            val maxBackoffDelay = 60 * 60 * 1000L // 1 hour
            val normalPollInterval = 60 * 60 * 1000L // 1 hour

            while (true) {
                var nextDelay = normalPollInterval
                try {
                    Logger.log(context, "HomeScreen", "Fetching latest versions...")
                    errorMessage = null // Clear previous error
                    val response = withContext(Dispatchers.IO) {
                        ApiClient.versionApiService.getLatestVersions()
                    }
                    latestVersions = response
                    Logger.log(context, "HomeScreen", "Successfully fetched latest versions: $response")
                    // On success, reset backoff delay
                    backoffDelay = 60 * 1000L
                } catch (e: Exception) {
                    e.printStackTrace()
                    val errorText = "获取版本信息失败: ${e.message}"
                    errorMessage = errorText
                    Logger.log(context, "HomeScreen", errorText)

                    // On failure, use the current backoff delay for the next attempt
                    nextDelay = backoffDelay
                    // Increase backoff for the *next* failure
                    backoffDelay = (backoffDelay * 2).coerceAtMost(maxBackoffDelay)
                    Logger.log(context, "HomeScreen", "Request failed. Retrying in ${nextDelay / 1000} seconds.")
                }
                delay(nextDelay)
            }
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(0.7f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                item {
                    CombinedCard(title = "主页", summary = "欢迎回来") {
                        XamlRenderer(nodes = nodes, modifier = Modifier.padding(horizontal = 20.dp))
                    }
                }
                if (enableVersionCheck) {
                    item {
                        CombinedCard(title = "Minecraft更新", summary = "查看最近的更新内容") {
                            when {
                                errorMessage != null -> {
                                    Text(text = errorMessage!!, modifier = Modifier.padding(16.dp))
                                }

                                latestVersions != null -> {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        latestVersions!!.release.let { release ->
                                            VersionInfoCard(versionInfo = release)
                                        }
                                        latestVersions!!.snapshot?.let { snapshot ->
                                            Spacer(modifier = Modifier.height(16.dp))
                                            VersionInfoCard(versionInfo = snapshot)
                                        }
                                    }
                                }

                                else -> {
                                    Text(text = "正在获取最新版本信息...", modifier = Modifier.padding(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        VerticalDivider()

        Box(modifier = Modifier.weight(0.3f).fillMaxHeight()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Image(
                        painter = painterResource(id = R.drawable.steve2),
                        contentDescription = "LanRhyme",
                        modifier = Modifier
                            .size(70.dp)
                            .glow(
                                color = MaterialTheme.colorScheme.primary,
                                enabled = isGlowEffectEnabled,
                                cornerRadius = 70.dp, // Make it circular
                                blurRadius = 20.dp
                            )
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    // Player Name
                    Text(
                        text = "LanRhyme",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = "微软账号",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Selected Version
                    Text(
                        text = "版本: 1.20.1",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Launch Button
                    ScalingActionButton(
                        onClick = { /* TODO: Handle launch */ },
                        modifier = Modifier.fillMaxWidth(),
                        text = "启动游戏"
                    )
                }
            }
        }
    }
}

@Composable
fun VersionInfoCard(versionInfo: VersionInfo) {
    val context = LocalContext.current
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            AsyncImage(
                model = versionInfo.versionImageLink,
                contentDescription = versionInfo.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        Modifier
                            .weight(1f, fill = false)
                            .padding(end = 8.dp)) {
                        Text(text = versionInfo.title, style = MaterialTheme.typography.titleLarge)
                        versionInfo.intro?.let { intro ->
                            Text(
                                text = intro,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    Text(text = versionInfo.versionType, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                versionInfo.translator?.let {
                    Text(
                        text = "翻译：$it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScalingActionButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo.officialLink)))
                        },
                        icon = Icons.AutoMirrored.Filled.Article,
                        text = "官方日志",
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    )
                    ScalingActionButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo.wikiLink)))
                        },
                        icon = Icons.Default.Book,
                        text = "Wiki",
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    )
                    ScalingActionButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo.serverJar)))
                        },
                        icon = Icons.Default.Download,
                        text = "服务端",
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

fun loadXaml(context: Context, fileName: String): String {
    val externalFile = File(context.getExternalFilesDir(null), fileName)

    if (externalFile.exists()) {
        return try {
            FileInputStream(externalFile).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    return try {
        context.assets.open(fileName).use { inputStream ->
            FileOutputStream(externalFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            // Now read the copied file
            FileInputStream(externalFile).bufferedReader().use { it.readText() }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        ""
    }
}

@Composable
fun VerticalDivider() {
    val dividerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
    ) {
        val brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, dividerColor, Color.Transparent),
            startY = 0f,
            endY = size.height
        )
        drawLine(
            brush = brush,
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = 0f, y = size.height),
            strokeWidth = 1.dp.toPx()
        )
    }
}
