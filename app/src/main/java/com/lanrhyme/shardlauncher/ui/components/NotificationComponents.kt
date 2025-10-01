package com.lanrhyme.shardlauncher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.ui.model.Notification
import com.lanrhyme.shardlauncher.ui.model.NotificationType
import com.lanrhyme.shardlauncher.ui.theme.Error40
import com.lanrhyme.shardlauncher.ui.theme.Error80
import com.lanrhyme.shardlauncher.ui.theme.ErrorSurface40
import com.lanrhyme.shardlauncher.ui.theme.ErrorSurface80
import com.lanrhyme.shardlauncher.ui.theme.Warning40
import com.lanrhyme.shardlauncher.ui.theme.Warning80
import com.lanrhyme.shardlauncher.ui.theme.WarningSurface40
import com.lanrhyme.shardlauncher.ui.theme.WarningSurface80

@Composable
fun NotificationItem(
    notification: Notification,
    onDismiss: (String) -> Unit,
    darkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (notification.type) {
        NotificationType.Warning -> if (darkTheme) WarningSurface40 else WarningSurface80
        NotificationType.Error -> if (darkTheme) ErrorSurface40 else ErrorSurface80
        else -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when (notification.type) {
        NotificationType.Warning -> if (darkTheme) Warning40 else Warning80
        NotificationType.Error -> if (darkTheme) Error40 else Error80
        else -> MaterialTheme.colorScheme.onSurface
    }

    val clickableModifier = if (notification is Notification.Clickable) {
        Modifier.clickable(onClick = notification.onClick)
    } else {
        Modifier
    }

    Surface(
        modifier = modifier.then(clickableModifier),
        shape = RoundedCornerShape(22.dp),
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = 3.dp,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = notification.title, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { onDismiss(notification.id) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss")
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(text = notification.message, style = MaterialTheme.typography.bodyMedium)

            if (notification is Notification.Progress) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = notification.progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
