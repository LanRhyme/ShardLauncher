package com.lanrhyme.shardlauncher.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.lanrhyme.shardlauncher.common.SidebarPosition
import com.lanrhyme.shardlauncher.ui.components.SegmentedNavigationBar
import com.lanrhyme.shardlauncher.ui.theme.ThemeColor
import dev.chrisbanes.haze.HazeState

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
    customPrimaryColor: Color,
    onCustomPrimaryColorChange: (Color) -> Unit,
    lightColorScheme: ColorScheme,
    darkColorScheme: ColorScheme,
    onLightColorSchemeChange: (ColorScheme) -> Unit,
    onDarkColorSchemeChange: (ColorScheme) -> Unit,
    enableBackgroundLightEffect: Boolean,
    onEnableBackgroundLightEffectChange: () -> Unit,
    lightEffectAnimationSpeed: Float,
    onLightEffectAnimationSpeedChange: (Float) -> Unit,
    enableBackgroundLightEffectCustomColor: Boolean,
    onEnableBackgroundLightEffectCustomColorChange: () -> Unit,
    backgroundLightEffectCustomColor: Color,
    onBackgroundLightEffectCustomColorChange: (Color) -> Unit,
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
    onIsGlowEffectEnabledChange: () -> Unit,
    isCardBlurEnabled: Boolean,
    onIsCardBlurEnabledChange: () -> Unit,
    useBmclapi: Boolean,
    onUseBmclapiChange: (Boolean) -> Unit,
    hazeState: HazeState
) {
    // 2. 添加状态来管理当前选中的页面
    var selectedPage by remember { mutableStateOf(SettingsPage.Launcher) }
    val pages = SettingsPage.entries

    Box(modifier = Modifier.fillMaxSize()) {
        // 4. 根据选中的页面显示不同的内容
        AnimatedContent(
            targetState = selectedPage,
            label = "Settings Page Animation",
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { page ->
            when (page) {
                SettingsPage.Launcher -> {
                    LauncherSettingsContent(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        sidebarPosition = sidebarPosition,
                        onPositionChange = onPositionChange,
                        themeColor = themeColor,
                        onThemeColorChange = onThemeColorChange,
                        customPrimaryColor = customPrimaryColor,
                        onCustomPrimaryColorChange = onCustomPrimaryColorChange,
                        lightColorScheme = lightColorScheme,
                        darkColorScheme = darkColorScheme,
                        onLightColorSchemeChange = onLightColorSchemeChange,
                        onDarkColorSchemeChange = onDarkColorSchemeChange,
                        enableBackgroundLightEffect = enableBackgroundLightEffect,
                        onEnableBackgroundLightEffectChange = onEnableBackgroundLightEffectChange,
                        lightEffectAnimationSpeed = lightEffectAnimationSpeed,
                        onLightEffectAnimationSpeedChange = onLightEffectAnimationSpeedChange,
                        enableBackgroundLightEffectCustomColor = enableBackgroundLightEffectCustomColor,
                        onEnableBackgroundLightEffectCustomColorChange = onEnableBackgroundLightEffectCustomColorChange,
                        backgroundLightEffectCustomColor = backgroundLightEffectCustomColor,
                        onBackgroundLightEffectCustomColorChange = onBackgroundLightEffectCustomColorChange,
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
                        onIsGlowEffectEnabledChange = onIsGlowEffectEnabledChange,
                        isCardBlurEnabled = isCardBlurEnabled,
                        onIsCardBlurEnabledChange = onIsCardBlurEnabledChange,
                        hazeState = hazeState
                    )
                }

                SettingsPage.Game -> {
                    GameSettingsContent(
                        useBmclapi = useBmclapi,
                        onUseBmclapiChange = onUseBmclapiChange,
                        animationSpeed = animationSpeed,
                        isCardBlurEnabled = isCardBlurEnabled,
                        hazeState = hazeState
                    )
                }

                SettingsPage.About -> {
                    AboutScreen(animationSpeed = animationSpeed, isCardBlurEnabled = isCardBlurEnabled, hazeState = hazeState)
                }

                SettingsPage.Other -> {
                    OtherSettingsContent(navController = navController, isCardBlurEnabled = isCardBlurEnabled, hazeState = hazeState)
                }
                // Other categories can be added later
                else -> { /* Placeholder for other settings pages */ }
            }
        }

        SegmentedNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            title = "设置",
            selectedPage = selectedPage,
            onPageSelected = { selectedPage = it },
            pages = pages.toList(),
            getTitle = { it.title }
        )
    }
}
