这是一个基于 Jetpack Compose 的Minecraft Java版启动器应用。
后续可能会转为kmp多平台项目，所以尽量使用支持多平台或计划在未来支持多平台的库。
尽量使用app/src/main/java/com/lanrhyme/shardlauncher/ui/components中的组件，避免重复造轮子。
启动器已完成部分前端页面，按照这些页面的样式写组件，后续页面也尽量遵循这些样式。
组件应尽量遵循 Material Design 3 指南，并提供一致的样式和行为。
组件应以可重用性为设计理念，简化整个应用的 UI 开发。
组件应尽量避免嵌套，使用轻量级修饰符链。
组件应合理管理状态，避免不必要的重组和重新渲染。
组件应控制动画生命周期，减少渲染开销。
组件应尽量避免使用实验性API，确保稳定性和兼容性。

- 合理状态管理实现组件复用
- 轻量级修饰符链，避免嵌套
- 控制动画生命周期，减少渲染开销

开发过程中可以大量参考此项目的代码：D:\IdeaProjects\ShardLauncher\ZalithLauncher2
这是一个基于 Jetpack Compose 的 Minecraft 启动器项目，代码质量较高且功能完善
注意是参考不是直接调用，可以导入这个项目使用的库，但不能直接调用这个项目的代码
还有ui方面还是按照原本的设计来，不要参考这个项目的ui设计

项目中注意多使用文档注释，方便后续维护和生成文档。
项目中注意代码结构清晰，方便后续维护和扩展。
项目中注意命名规范，遵循Kotlin命名规范。

以下是本项目的梗概：

# ShardLauncher 项目概述

## 项目简介

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

## 项目结构

```
ShardLauncher/
├── app/                  # 主应用模块
│   ├── src/main/
│   │   ├── java/         # Kotlin 源代码
│   │   │   └── com.lanrhyme.shardlauncher/
│   │   │       ├── MainActivity.kt          # 应用主入口
│   │   │       ├── ShardLauncherApp.kt      # Application 类
│   │   │       ├── api/                     # 网络 API 接口定义
│   │   │       ├── common/                  # 公共数据类（如 SidebarPosition）
│   │   │       ├── data/                    # 数据层（如 SettingsRepository）
│   │   │       ├── model/                   # 数据模型
│   │   │       ├── ui/                      # UI 层
│   │   │       │   ├── components/          # 可复用 UI 组件
│   │   │       │   ├── navigation/          # 导航相关
│   │   │       │   ├── settings/            # 设置界面
│   │   │       │   ├── downloads/           # 下载界面
│   │   │       │   ├── account/             # 账户界面
│   │   │       │   ├── home/                # 主页界面
│   │   │       │   ├── notification/        # 通知系统
│   │   │       │   ├── developeroptions/    # 开发者选项
│   │   │       │   ├── crash/               # 崩溃处理
│   │   │       │   ├── custom/              # 自定义 XAML 解析器
│   │   │       │   ├── theme/               # 主题定义
│   │   │       │   └── LocalSettingsProvider.kt # 本地设置提供者
│   │   │       └── utils/                   # 工具类
│   │   └── res/          # 资源文件（图片、字符串、主题等）
│   └── build.gradle.kts  # 应用模块构建脚本
├── gradle/               # Gradle Wrapper 和版本配置
├── build.gradle.kts      # 项目级构建脚本
├── settings.gradle.kts   # 项目设置
├── gradle.properties     # Gradle 属性
└── README.md             # 项目说明
```

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

### 构建配置

*   **构建工具:** Gradle (Kotlin DSL)
*   **ABI 分包:** 项目配置了 `splits.abi` 来为不同的 CPU 架构 (x86, x86_64, armeabi-v7a, arm64-v8a) 生成独立的 APK，并生成一个包含所有架构的通用 APK (`universalApk`).
*   **Git 信息注入:** `app/build.gradle.kts` 中定义了 `gitBranch()`, `gitHash()`, `gitCommitDate()` 函数，用于在构建时将 Git 信息注入到应用资源中。

## 开发约定

*   **UI 开发:** 使用 Jetpack Compose 进行声明式 UI 开发。
*   **状态管理:** 利用 Compose 的 `State` 和 `ViewModel` (如果使用) 来管理 UI 状态。
*   **导航:** 使用 `androidx.navigation:navigation-compose` 进行单 Activity 多 Composable 的页面导航。
*   **主题:** 使用 Material Design 3 主题系统，并支持深色模式和多种主题颜色自定义。
*   **设置存储:** 使用 `SharedPreferences` (通过 `SettingsRepository` 封装) 来持久化用户设置。
