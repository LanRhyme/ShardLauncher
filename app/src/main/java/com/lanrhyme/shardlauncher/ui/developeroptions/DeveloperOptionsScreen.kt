package com.lanrhyme.shardlauncher.ui.developeroptions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.lanrhyme.shardlauncher.manager.NotificationManager
import com.lanrhyme.shardlauncher.ui.components.SliderLayout
import com.lanrhyme.shardlauncher.ui.components.TitleAndSummary
import com.lanrhyme.shardlauncher.ui.model.Notification
import com.lanrhyme.shardlauncher.ui.model.NotificationType

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
                summary = "调整应用内动画速率"
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
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expanded = true }, modifier = Modifier.align(Alignment.Center)) {
            Text("Send Test Notification")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val notificationTypes = NotificationType.values()
            notificationTypes.forEach { type ->
                DropdownMenuItem(onClick = {
                    sendTestNotification(type)
                    expanded = false
                }, text = { Text(type.name) })
            }
        }
    }
}

private fun sendTestNotification(type: NotificationType) {
    val notification = when (type) {
        NotificationType.Temporary -> Notification.Temporary("Temporary Notification", "This is a temporary notification.")
        NotificationType.Normal -> Notification.Normal("Normal Notification", "This is a normal notification.")
        NotificationType.Progress -> Notification.Progress("Progress Notification", "This is a progress notification.", 0.5f)
        NotificationType.Warning -> Notification.Warning("Warning Notification", "This is a warning notification.")
        NotificationType.Error -> Notification.Error("Error Notification", "This is an error notification.")
    }
    NotificationManager.show(notification)
}
