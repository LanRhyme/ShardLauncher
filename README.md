# ShardLauncher

[English Version (README_en.md)](README_en.md)

[![开发构建状态](https://github.com/LanRhyme/ShardLauncher/actions/workflows/development.yml/badge.svg?branch=master)](https://github.com/LanRhyme/ShardLauncher/actions/workflows/development.yml)

ShardLauncher 是一个基于 Jetpack Compose 的 Minecraft Java 版 Android 启动器应用。它提供了现代化的用户界面和丰富的自定义选项。

## 核心功能

*   **多种登录方式:** 支持微软和离线登录。
*   **游戏管理:** 自动下载和管理 Minecraft 游戏文件，包括客户端、资源和库。
*   **高度可定制的 UI:**
    *   深色模式和多种主题颜色
    *   可自定义的启动器背景（图片或视频）
    *   可调整的 UI 动画速度和缩放比例
    *   可自定义的侧边栏位置
*   **版本检查:** 可选择是否开启 Minecraft 最新版本信息的获取。

## 技术栈

*   **语言:** Kotlin
*   **UI 框架:** Jetpack Compose
*   **设计语言:** Material Design 3
*   **主要依赖库:**
    *   `androidx.navigation:navigation-compose` 用于页面导航
    *   `io.coil-kt:coil-compose` 用于图片加载
    *   `com.squareup.retrofit2:retrofit` 用于网络请求
    *   `androidx.media3:media3-exoplayer` 用于视频背景播放

## 构建与运行

### 环境要求

*   Android Studio (推荐最新稳定版)
*   Android SDK (API 35 或更高)
*   JDK 11

### 构建步骤

1.  克隆项目仓库：
    ```bash
    git clone https://github.com/LanRhyme/ShardLauncher.git
    ```
2.  在 Android Studio 中打开项目。
3.  等待 Gradle 同步完成。
4.  连接 Android 设备或启动模拟器。
5.  点击 Android Studio 中的 "Run" 按钮 (绿色三角形) 或使用快捷键 `Shift + F10` 构建并安装应用。

## 开发约定

*   **UI 开发:** 使用 Jetpack Compose 进行声明式 UI 开发。
*   **状态管理:** 利用 Compose 的 `State` 和 `ViewModel` (如果使用) 来管理 UI 状态。
*   **导航:** 使用 `androidx.navigation:navigation-compose` 进行单 Activity 多 Composable 的页面导航。
*   **主题:** 使用 Material Design 3 主题系统，并支持深色模式和多种主题颜色自定义。
*   **设置存储:** 使用 `SharedPreferences` (通过 `SettingsRepository` 封装) 来持久化用户设置。
