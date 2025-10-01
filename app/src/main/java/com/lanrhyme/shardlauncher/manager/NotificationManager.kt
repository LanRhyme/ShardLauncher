package com.lanrhyme.shardlauncher.manager

import com.lanrhyme.shardlauncher.ui.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object NotificationManager {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    fun show(notification: Notification) {
        _notifications.update { it + notification }
    }

    fun dismiss(notificationId: String) {
        _notifications.update { it.filterNot { n -> n.id == notificationId } }
    }
}
