package com.lanrhyme.shardlauncher.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lanrhyme.shardlauncher.common.SidebarPosition
import com.lanrhyme.shardlauncher.ui.components.SimpleListLayout
import com.lanrhyme.shardlauncher.ui.components.SwitchLayout
import com.lanrhyme.shardlauncher.ui.navigation.Screen
import com.lanrhyme.shardlauncher.ui.theme.ThemeColor

// 1. 定义设置页面分类
enum class SettingsPage(val title: String) {
    Launcher("启动器设置"),
    Game("全局游戏设置"),
    Controls("控制设置"),
    About("关于"),
    Other("其他")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    sidebarPosition: SidebarPosition,
    onPositionChange: (SidebarPosition) -> Unit,
    themeColor: ThemeColor, // Hoisted state for theme color
    onThemeColorChange: (ThemeColor) -> Unit, // Hoisted callback for theme color
    enableBackgroundLightEffect: Boolean,
    onEnableBackgroundLightEffectChange: () -> Unit
) {
    // 2. 添加状态来管理当前选中的页面
    var selectedPage by remember { mutableStateOf(SettingsPage.Launcher) }
    val pages = SettingsPage.entries

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "设置",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 16.dp)
            )
            PrimaryTabRow(
                selectedTabIndex = selectedPage.ordinal,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp)),
            ) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        selected = selectedPage.ordinal == index,
                        onClick = { selectedPage = pages[index] },
                        text = { Text(text = page.title) }
                    )
                }
            }
        }

        // 4. 根据选中的页面显示不同的内容
        AnimatedContent(targetState = selectedPage, label = "Settings Page Animation", transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }) { page ->
            when (page) {
                SettingsPage.Launcher -> {
                    LauncherSettingsContent(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        sidebarPosition = sidebarPosition,
                        onPositionChange = onPositionChange,
                        themeColor = themeColor,
                        onThemeColorChange = onThemeColorChange,
                        enableBackgroundLightEffect = enableBackgroundLightEffect,
                        onEnableBackgroundLightEffectChange = onEnableBackgroundLightEffectChange
                    )
                }
                SettingsPage.Other -> {
                    OtherSettingsContent(navController = navController)
                }
                // Other categories can be added later
                else -> { /* Placeholder for other settings pages */ }
            }
        }
    }
}

// 将原有的设置内容提取到一个独立的Composable函数中
@Composable
private fun LauncherSettingsContent(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    sidebarPosition: SidebarPosition,
    onPositionChange: (SidebarPosition) -> Unit,
    themeColor: ThemeColor,
    onThemeColorChange: (ThemeColor) -> Unit,
    enableBackgroundLightEffect: Boolean,
    onEnableBackgroundLightEffectChange: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "显示设置",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        item {
            SwitchLayout(
                title = "深色模式",
                summary = if (isDarkTheme) "已开启" else "已关闭",
                checked = isDarkTheme,
                onCheckedChange = onThemeToggle
            )
        }
        item {
            SwitchLayout(
                title = "背景光效",
                summary = if (enableBackgroundLightEffect) "已开启" else "已关闭",
                checked = enableBackgroundLightEffect,
                onCheckedChange = onEnableBackgroundLightEffectChange
            )
        }
        item {
            SimpleListLayout(
                title = "侧边栏位置",
                items = SidebarPosition.entries,
                selectedItem = sidebarPosition,
                onValueChange = onPositionChange,
                getItemText = {
                    when (it) {
                        SidebarPosition.Left -> "左侧"
                        SidebarPosition.Right -> "右侧"
                    }
                }
            )
        }
        item {
            SimpleListLayout(
                title = "主题颜色",
                items = ThemeColor.entries.toList(),
                selectedItem = themeColor,
                onValueChange = onThemeColorChange,
                getItemText = { it.title }
            )
        }
    }
}

@Composable
private fun OtherSettingsContent(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "高级",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        item {
            SimpleListLayout(
                title = "开发者选项",
                items = listOf(Unit),
                selectedItem = Unit,
                onValueChange = { navController.navigate(Screen.DeveloperOptions.route) },
                getItemText = { "点击进入开发者选项" } 
            )
        }
    }
}
