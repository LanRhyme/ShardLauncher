# ShardLauncher 项目

## 1. 项目概述

本项目名为 **ShardLauncher**，是一款适用于 Android 平台的 Minecraft Java版 启动器。

## 2. 核心技术

- **语言**: Kotlin
- **UI 工具包**: Jetpack Compose
- **构建系统**: Gradle

## 3. 架构设计

本启动器遵循前后端分离的设计模式：

- **前端**: 用户界面和用户体验逻辑将使用 Kotlin 和 Jetpack Compose 从零开始构建。前端源代码位于 `app/` 目录下。
- **后端**: 核心的游戏启动、版本管理和安装逻辑由 **Amethyst-Android** 项目提供。后端代码位于 `backend/amethyst/` 目录下，应被视为一个库或依赖项。
- **参考与灵感**: 设计、功能和整体用户体验应主要参考 **FoldCraftLauncher (FCL)**。

## 4. AI 开发指南

在协助开发时，请遵循以下原则：

- **技术栈**: 在 `app` 模块中，所有新的UI和应用逻辑都必须严格使用 Kotlin 和 Jetpack Compose。
- **代码风格**: 编写整洁、地道的 Kotlin 代码。遵循现代 Android 开发最佳实践和 Material Design 3 设计准则。
- **后端交互**: 除非有明确要求，否则不要修改 `backend/amethyst/` 中的后端逻辑。应将其视为用于游戏启动的稳定API。所有与 Minecraft 相关的交互（例如，安装、启动、管理版本）都应委托给后端处理。
- **开发焦点**: 主要的开发焦点是在 `app` 模块中构建一个高质量、用户友好的前端，并确保其能正确地与 Amethyst 后端进行交互。