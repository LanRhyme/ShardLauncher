package com.lanrhyme.shardlauncher.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "主页", Icons.Filled.Home)
    object Version : Screen("version", "版本", Icons.Filled.Info)
    object Download : Screen("download", "下载", Icons.Filled.Download)
    object Online : Screen("online", "联机", Icons.Filled.Cloud)
    object Settings : Screen("settings", "设置", Icons.Filled.Settings)
    object DeveloperOptions : Screen("developer_options", "开发者选项", Icons.Filled.DeveloperMode)
}

val navigationItems = listOf(
    Screen.Home,
    Screen.Version,
    Screen.Download,
    Screen.Online,
    Screen.Settings
)
