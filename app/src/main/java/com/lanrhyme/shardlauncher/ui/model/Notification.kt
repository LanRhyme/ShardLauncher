package com.lanrhyme.shardlauncher.ui.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
sealed class Notification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val type: NotificationType
) {
    // 临时通知
    class Temporary(
        title: String,
        message: String,
    ) : Notification(title = title, message = message, type = NotificationType.Temporary)

    // 普通通知
    class Normal(
        title: String,
        message: String,
    ) : Notification(title = title, message = message, type = NotificationType.Normal)

    // 进度条通知
    class Progress(
        title: String,
        message: String,
        val progress: Float,
    ) : Notification(title = title, message = message, type = NotificationType.Progress)

    // 可点击通知
    class Clickable(
        title: String,
        message: String,
        val onClick: () -> Unit,
        val subType: NotificationType = NotificationType.Normal,
    ) : Notification(title = title, message = message, type = subType)

    // 警告通知
    class Warning(
        title: String,
        message: String,
    ) : Notification(title = title, message = message, type = NotificationType.Warning)

    // 错误通知
    class Error(
        title: String,
        message: String,
    ) : Notification(title = title, message = message, type = NotificationType.Error)
}

enum class NotificationType {
    Temporary,
    Normal,
    Progress,
    Warning,
    Error
}
