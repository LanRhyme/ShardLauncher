package com.lanrhyme.shardlauncher.ui.home

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.R
import com.lanrhyme.shardlauncher.ui.components.CombinedCard
import com.lanrhyme.shardlauncher.ui.custom.XamlRenderer
import com.lanrhyme.shardlauncher.ui.custom.parseXaml
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val xamlContent = remember { loadXaml(context, "home.xaml") }
    val nodes = parseXaml(xamlContent)

    Row(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(0.7f)) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                item {
                    CombinedCard(title = "主页", summary = "欢迎回来") {
                        XamlRenderer(nodes = nodes, modifier = Modifier.padding(horizontal = 20.dp))
                    }
                }
            }
        }

        VerticalDivider()

        Box(modifier = Modifier.weight(0.3f).fillMaxHeight()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Player Avatar
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder
                    contentDescription = "Player Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Player Name
                Text(
                    text = "Player Name",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Account Type
                Text(
                    text = "Account Type",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Selected Version
                Text(
                    text = "Selected Version: 1.20.1",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Launch Button
                Button(
                    onClick = { /* TODO: Handle launch */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "启动游戏")
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
