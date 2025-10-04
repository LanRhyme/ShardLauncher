# ShardLauncher 项目

这是一个基于 Jetpack Compose 的Minecraft Java版启动器应用。
后续可能会转为kmp多平台项目，所以尽量使用支持多平台或计划在未来支持多平台的库。
尽量使用app/src/main/java/com/lanrhyme/shardlauncher/ui/components中的组件，避免重复造轮子。
本启动器的后端将会使用FCLCore、FCLibrary和FCLauncher库（ https://github.com/FCL-Team/FoldCraftLauncher ），启动器引擎将使用FCLauncher中的 LWJGL-Pojav 模块，不使用 LWJGL-Boat 模块。
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
