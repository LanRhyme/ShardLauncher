package com.lanrhyme.shardlauncher.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "主页", Icons.Filled.Home)
    object Settings : Screen("settings", "设置", Icons.Filled.Settings)
    object DeveloperOptions : Screen("developer_options", "开发者选项", Icons.Filled.DeveloperMode)
}

val navigationItems = listOf(
    Screen.Home,
    Screen.Settings
)
