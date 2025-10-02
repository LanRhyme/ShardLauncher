# 通知系统使用文档

## 概述

本应用包含一个完全自定义的应用内通知系统，使用 Jetpack Compose 构建。它负责在应用内显示临时弹窗、管理一个持久化的通知列表，并支持多种通知类型。

系统的核心是单例对象 `NotificationManager`，所有通知操作都应通过它进行。

## 核心组件

- **`manager/NotificationManager.kt`**: 全局单例，用于调度和管理通知。这是与通知系统交互的唯一入口。
- **`ui/model/Notification.kt`**: 定义了 `Notification` 数据类及其 `NotificationType` 枚举，是通知的数据模型。
- **`ui/components/NotificationComponents.kt`**: 包含 `NotificationItem` Composable，负责渲染单个通知卡片的UI。
- **`ui/NotificationDialog.kt`**: 包含 `NotificationDialog` Composable，负责渲染通知详情的弹窗。
- **`MainActivity.kt`**: 
    - `NotificationPopupHost`: 负责在屏幕右上角显示临时通知弹窗。
    - `NotificationPanel`: 负责在主侧边栏展开时，在屏幕另一侧滑出持久化通知的列表。

---

## 如何使用

所有操作都通过调用 `NotificationManager` 的方法来完成。

### 1. 数据模型

`Notification` 数据类结构如下：

```kotlin
data class Notification(
    val id: String = UUID.randomUUID().toString(), // 唯一ID，自动生成
    val title: String,                          // 标题
    val message: String,                        // 消息内容
    val type: NotificationType,                 // 通知类型
    val progress: Float? = null,                // 进度 (0.0 to 1.0)
    val isClickable: Boolean = false,           // 是否可点击
    val onClick: (() -> Unit)? = null           // 点击事件回调
)

enum class NotificationType {
    Temporary, // 临时通知 (3秒后消失)
    Normal,    // 普通通知 (保留在列表中)
    Progress,  // 进度条通知
    Warning,   // 警告样式
    Error      // 错误样式
}
```

### 2. 发送通知

#### 发送一个简单的临时通知

临时通知会弹出，3秒后自动消失，不会保留在侧边栏的通知列表中。

```kotlin
NotificationManager.show(
    Notification(
        title = "操作成功",
        message = "你的设置已保存。",
        type = NotificationType.Temporary
    )
)
```

#### 发送一个普通的持久化通知

这种通知会保留在列表中，直到用户手动清除。

```kotlin
NotificationManager.show(
    Notification(
        title = "欢迎",
        message = "欢迎使用 ShardLauncher！",
        type = NotificationType.Normal
    )
)
```

#### 发送并更新进度条通知

发送一个进度通知，并随后更新它的进度。

```kotlin
// 1. 创建一个带初始进度的通知并显示
val progressNotification = Notification(
    title = "正在下载...",
    message = "minecraft-1.20.1.jar",
    type = NotificationType.Progress,
    progress = 0.1f
)
NotificationManager.show(progressNotification)

// 2. 在下载过程中，使用同一个通知的 ID 来更新进度
// (你需要保存 progressNotification.id)
NotificationManager.updateProgress(progressNotification.id, 0.5f) // 更新到 50%

// 3. 下载完成后，将它作为一个普通通知或临时通知替换，或直接移除
NotificationManager.dismiss(progressNotification.id)
NotificationManager.show(
    Notification(
        title = "下载完成",
        message = "minecraft-1.20.1.jar 已安装。",
        type = NotificationType.Normal
    )
)
```

#### 发送可点击的通知

`isClickable` 和 `onClick` 属性可以和任何 `type` 叠加使用。
当 `isClickable` 为 `true` 时，`NotificationUI` 会自动处理点击事件，并显示一个包含通知详情的弹窗。如果`onClick`为空，则会使用默认的弹窗行为。如果需要自定义点击行为，可以提供一个`onClick` lambda。

```kotlin
NotificationManager.show(
    Notification(
        title = "发现新版本",
        message = "点击查看详情。",
        type = NotificationType.Warning, // 这是一个“可点击的警告”
        isClickable = true,
        onClick = { 
            // 如果onClick不为null,则会覆盖默认的弹窗行为,执行这里的逻辑
        }
    )
)
```

### 3. 管理通知

#### 移除单个通知

```kotlin
// onDismiss 回调中已实现，通常不需要手动调用
NotificationManager.dismiss(notificationId)
```

#### 清除所有持久化通知

```kotlin
// “全部清除”按钮已实现
NotificationManager.clearAll()
```
