package com.lanrhyme.shardlauncher.ui.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.common.SidebarPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NotificationPanel(
    isVisible: Boolean,
    sidebarPosition: SidebarPosition
) {
    val panelAlignment = if (sidebarPosition == SidebarPosition.Left) Alignment.CenterEnd else Alignment.CenterStart
    val enterAnimation = if (sidebarPosition == SidebarPosition.Left) slideInHorizontally(initialOffsetX = { it }) else slideInHorizontally(initialOffsetX = { -it })
    val exitAnimation = if (sidebarPosition == SidebarPosition.Left) slideOutHorizontally(targetOffsetX = { it }) else slideOutHorizontally(targetOffsetX = { -it })

    val allNotifications by NotificationManager.notifications.collectAsState()
    var dialogNotification by remember { mutableStateOf<Notification?>(null) }
    val persistentNotifications = remember(allNotifications) {
        allNotifications.filter { it.type != NotificationType.Temporary }
    }

    if (dialogNotification != null) {
        NotificationDialog(notification = dialogNotification!!, onDismiss = { dialogNotification = null })
    }

    val visibleItems = remember { mutableStateListOf<String>() }

    LaunchedEffect(persistentNotifications, isVisible) {
        if (isVisible) {
            val coroutineScope = this
            visibleItems.clear()
            persistentNotifications.forEachIndexed { index, item ->
                coroutineScope.launch {
                    delay(index * 50L + 100L) // Stagger delay
                    visibleItems.add(item.id)
                }
            }
        } else {
            visibleItems.clear()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = enterAnimation,
        exit = exitAnimation
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = panelAlignment) {
            val shape = if (sidebarPosition == SidebarPosition.Left) {
                RoundedCornerShape(topStart = 22.dp, bottomStart = 22.dp)
            } else {
                RoundedCornerShape(topEnd = 22.dp, bottomEnd = 22.dp)
            }

            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.4f),
                shape = shape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 12.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (persistentNotifications.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("没有通知")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(persistentNotifications, key = { it.id }) { notification ->
                                AnimatedVisibility(
                                    visible = notification.id in visibleItems,
                                    enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(initialOffsetY = { it / 2 }),
                                    exit = fadeOut(animationSpec = tween(durationMillis = 200))
                                ) {
                                    NotificationItem(
                                        notification = notification.copy(onClick = {
                                            dialogNotification = notification
                                        }),
                                        onDismiss = { NotificationManager.dismiss(it) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        // Clear All Button
                        IconButton(
                            onClick = { NotificationManager.clearAll() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "全部清除")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationPopupHost() {
    val allNotifications by NotificationManager.notifications.collectAsState()
    val seenPopupIds by NotificationManager.seenPopupIds.collectAsState()
    var dialogNotification by remember { mutableStateOf<Notification?>(null) }

    val notificationsToShowAsPopup = allNotifications.filter { it.id !in seenPopupIds }

    if (dialogNotification != null) {
        NotificationDialog(notification = dialogNotification!!, onDismiss = { dialogNotification = null })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, end = 16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            notificationsToShowAsPopup.forEach { notification ->
                key(notification.id) {
                    PopupNotificationItem(
                        notification = notification,
                        onDismiss = { id, type ->
                            NotificationManager.addSeenPopupId(id)
                            if (type == NotificationType.Temporary) {
                                NotificationManager.dismiss(id)
                            }
                        },
                        onClick = {
                            dialogNotification = notification
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PopupNotificationItem(
    notification: Notification,
    onDismiss: (String, NotificationType) -> Unit,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(notification.id) {
        visible = true
        delay(3000)
        visible = false
        delay(500) // wait for exit animation
        onDismiss(notification.id, notification.type)
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally { it } + fadeIn(),
        exit = slideOutHorizontally { it } + fadeOut()
    ) {
        NotificationItem(
            notification = notification.copy(onClick = onClick),
            onDismiss = { onDismiss(notification.id, notification.type) },
            modifier = Modifier.width(350.dp)
        )
    }
}
