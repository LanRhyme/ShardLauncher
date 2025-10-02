package com.lanrhyme.shardlauncher.ui.developeroptions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.ui.notification.NotificationManager
import com.lanrhyme.shardlauncher.ui.components.SliderLayout
import com.lanrhyme.shardlauncher.ui.components.TitleAndSummary
import com.lanrhyme.shardlauncher.ui.notification.Notification
import com.lanrhyme.shardlauncher.ui.notification.NotificationType

@Composable
fun DeveloperOptionsScreen(
    animationSpeed: Float,
    onAnimationSpeedChange: (Float) -> Unit
) {
    val animatedSpeed by animateFloatAsState(targetValue = animationSpeed)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleAndSummary(
                title = "开发者选项",
                summary = "一些用于测试的功能，它们在将来可能被移除"
            )

            Spacer(modifier = Modifier.height(32.dp))

            SliderLayout(
                value = animationSpeed,
                onValueChange = { onAnimationSpeedChange(it) },
                valueRange = 0.5f..2f,
                steps = 14,
                title = "动画速率",
                summary = "控制 UI 动画的播放速度",
                displayValue = animatedSpeed
            )

            Spacer(modifier = Modifier.height(16.dp))

            TestNotificationSender()
        }
    }
}

@Composable
private fun TestNotificationSender() {
    var progress by remember { mutableStateOf(0f) }
    var progressNotificationId by remember { mutableStateOf<String?>(null) }

    val buttons = remember {
        listOf(
            "Temporary" to { NotificationManager.show(Notification(title = "Temporary", message = "Disappears after 3s", type = NotificationType.Temporary)) },
            "Normal" to { NotificationManager.show(Notification(title = "Normal", message = "Stays in the list", type = NotificationType.Normal)) },
            "Warning" to { NotificationManager.show(Notification(title = "Warning", message = "A warning message", type = NotificationType.Warning)) },
            "Error" to { NotificationManager.show(Notification(title = "Error", message = "An error message", type = NotificationType.Error)) },
            "Clickable" to { NotificationManager.show(Notification(title = "Clickable", message = "Click me!", type = NotificationType.Normal, isClickable = true, onClick = {})) },
            "Clickable Warning" to { NotificationManager.show(Notification(title = "Clickable Warning", message = "A clickable warning", type = NotificationType.Warning, isClickable = true, onClick = {})) },
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TitleAndSummary(title = "Test Notifications", summary = "Send different types of notifications")
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(buttons) { (text, onClick) ->
                Button(onClick = onClick) {
                    Text(text)
                }
            }
            item {
                Button(onClick = {
                    val notification = Notification(title = "Progress", message = "Updating...", type = NotificationType.Progress, progress = progress, isClickable = true, onClick = {})
                    progressNotificationId = notification.id
                    NotificationManager.show(notification)
                }) {
                    Text("Progress")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SliderLayout(
            value = progress,
            onValueChange = {
                progress = it
                progressNotificationId?.let { id ->
                    NotificationManager.updateProgress(id, it)
                }
            },
            title = "Update Progress",
            summary = "For the last progress notification sent"
        )
    }
}
