package com.lanrhyme.shardlauncher.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.lanrhyme.shardlauncher.common.SidebarPosition
import com.lanrhyme.shardlauncher.ui.components.SegmentedNavigationBar
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
    onEnableBackgroundLightEffectChange: () -> Unit,
    lightEffectAnimationSpeed: Float,
    onLightEffectAnimationSpeedChange: (Float) -> Unit,
    animationSpeed: Float,
    onAnimationSpeedChange: (Float) -> Unit,
    launcherBackgroundUri: String?,
    onLauncherBackgroundUriChange: (String?) -> Unit,
    launcherBackgroundBlur: Float,
    onLauncherBackgroundBlurChange: (Float) -> Unit,
    launcherBackgroundBrightness: Float,
    onLauncherBackgroundBrightnessChange: (Float) -> Unit,
    launcherBackgroundVideoVolume: Float,
    onLauncherBackgroundVideoVolumeChange: (Float) -> Unit,
    enableVersionCheck: Boolean,
    onEnableVersionCheckChange: () -> Unit,
    uiScale: Float,
    onUiScaleChange: (Float) -> Unit,
    isGlowEffectEnabled: Boolean,
    onIsGlowEffectEnabledChange: () -> Unit
) {
    // 2. 添加状态来管理当前选中的页面
    var selectedPage by remember { mutableStateOf(SettingsPage.Launcher) }
    val pages = SettingsPage.entries

    Column(modifier = Modifier.fillMaxSize()) {
        SegmentedNavigationBar(
            title = "设置",
            selectedPage = selectedPage,
            onPageSelected = { selectedPage = it },
            pages = pages.toList(),
            getTitle = { it.title }
        )

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
                        onEnableBackgroundLightEffectChange = onEnableBackgroundLightEffectChange,
                        lightEffectAnimationSpeed = lightEffectAnimationSpeed,
                        onLightEffectAnimationSpeedChange = onLightEffectAnimationSpeedChange,
                        animationSpeed = animationSpeed,
                        onAnimationSpeedChange = onAnimationSpeedChange,
                        launcherBackgroundUri = launcherBackgroundUri,
                        onLauncherBackgroundUriChange = onLauncherBackgroundUriChange,
                        launcherBackgroundBlur = launcherBackgroundBlur,
                        onLauncherBackgroundBlurChange = onLauncherBackgroundBlurChange,
                        launcherBackgroundBrightness = launcherBackgroundBrightness,
                        onLauncherBackgroundBrightnessChange = onLauncherBackgroundBrightnessChange,
                        launcherBackgroundVideoVolume = launcherBackgroundVideoVolume,
                        onLauncherBackgroundVideoVolumeChange = onLauncherBackgroundVideoVolumeChange,
                        enableVersionCheck = enableVersionCheck,
                        onEnableVersionCheckChange = onEnableVersionCheckChange,
                        uiScale = uiScale,
                        onUiScaleChange = onUiScaleChange,
                        isGlowEffectEnabled = isGlowEffectEnabled,
                        onIsGlowEffectEnabledChange = onIsGlowEffectEnabledChange
                    )
                }

                SettingsPage.About -> {
                    AboutScreen()
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