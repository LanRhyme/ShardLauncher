package com.lanrhyme.shardlauncher.ui.settings

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.lanrhyme.shardlauncher.common.SidebarPosition
import com.lanrhyme.shardlauncher.ui.components.CollapsibleCard
import com.lanrhyme.shardlauncher.ui.components.ScalingActionButton
import com.lanrhyme.shardlauncher.ui.components.SimpleListLayout
import com.lanrhyme.shardlauncher.ui.components.SliderLayout
import com.lanrhyme.shardlauncher.ui.components.SwitchLayout
import com.lanrhyme.shardlauncher.ui.components.animatedAppearance
import com.lanrhyme.shardlauncher.ui.composables.HsvColorPicker
import com.lanrhyme.shardlauncher.ui.composables.ThemeColorEditor
import com.lanrhyme.shardlauncher.ui.theme.ColorPalettes
import com.lanrhyme.shardlauncher.ui.theme.ThemeColor
import dev.chrisbanes.haze.HazeState
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LauncherSettingsContent(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    sidebarPosition: SidebarPosition,
    onPositionChange: (SidebarPosition) -> Unit,
    themeColor: ThemeColor,
    onThemeColorChange: (ThemeColor) -> Unit,
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
    hazeState: HazeState
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = animationSpeed,
        label = "Animation Speed",
        animationSpec = tween((1000 / animationSpeed).toInt())
    )
    val context = LocalContext.current
    val backgroundFileName = "launcher_background.jpg"
    val backgroundVideoName = "launcher_background.mp4"

    val isBackgroundVideo = launcherBackgroundUri?.endsWith(".mp4") == true

    var showColorPickerDialog by remember { mutableStateOf(false) }
    var tempLightColorScheme by remember(lightColorScheme) { mutableStateOf(lightColorScheme) }
    var tempDarkColorScheme by remember(darkColorScheme) { mutableStateOf(darkColorScheme) }

    var showLightEffectColorPickerDialog by remember { mutableStateOf(false) }
    var tempLightEffectColor by remember(backgroundLightEffectCustomColor) { mutableStateOf(backgroundLightEffectCustomColor) }

    if (showLightEffectColorPickerDialog) {
        AlertDialog(
            onDismissRequest = { showLightEffectColorPickerDialog = false },
            title = { Text("自定义光效颜色") },
            text = {
                HsvColorPicker(
                    color = tempLightEffectColor,
                    onColorSelected = { tempLightEffectColor = it }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onBackgroundLightEffectCustomColorChange(tempLightEffectColor)
                    showLightEffectColorPickerDialog = false
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLightEffectColorPickerDialog = false }) {
                    Text("取消")
                }
            }
        )
    }


    if (showColorPickerDialog) {
        AlertDialog(
            onDismissRequest = { showColorPickerDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.width(650.dp),
            title = { Text("自定义主题颜色") },
            text = {
                Box(modifier = Modifier.height(500.dp)) {
                    ThemeColorEditor(
                        lightColorScheme = tempLightColorScheme,
                        darkColorScheme = tempDarkColorScheme,
                        onLightColorSchemeChange = { tempLightColorScheme = it },
                        onDarkColorSchemeChange = { tempDarkColorScheme = it }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onLightColorSchemeChange(tempLightColorScheme)
                    onDarkColorSchemeChange(tempDarkColorScheme)
                    // also update the primary color for generation logic
                    onCustomPrimaryColorChange(tempLightColorScheme.primary)
                    onThemeColorChange(ThemeColor.Custom)
                    showColorPickerDialog = false
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        tempLightColorScheme = ColorPalettes.Green.lightColorScheme
                        tempDarkColorScheme = ColorPalettes.Green.darkColorScheme
                    }) {
                        Text("重置")
                    }
                    TextButton(onClick = { showColorPickerDialog = false }) {
                        Text("取消")
                    }
                }
            }
        )
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                val destinationFile = File(context.getExternalFilesDir(null), backgroundFileName)
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        FileOutputStream(destinationFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    onLauncherBackgroundUriChange(Uri.fromFile(destinationFile).toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Optionally, notify the user of the failure
                }
            }
        }
    )

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                val destinationFile = File(context.getExternalFilesDir(null), backgroundVideoName)
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        FileOutputStream(destinationFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    onLauncherBackgroundUriChange(Uri.fromFile(destinationFile).toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Optionally, notify the user of the failure
                }
            }
        }
    )

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
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .animatedAppearance(0, animationSpeed)
            )
        }
        item { 
            SwitchLayout(
                modifier = Modifier
                    .animatedAppearance(1, animationSpeed),
                title = "深色模式",
                summary = if (isDarkTheme) "已开启" else "已关闭",
                checked = isDarkTheme,
                onCheckedChange = onThemeToggle,
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
        item { 
            SwitchLayout(
                modifier = Modifier
                    .animatedAppearance(2, animationSpeed),
                title = "获取Minecraft最新更新信息",
                summary = "来源于news.bugjump.net",
                checked = enableVersionCheck,
                onCheckedChange = { onEnableVersionCheckChange() },
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
        item { 
            SimpleListLayout(
                modifier = Modifier
                    .animatedAppearance(3, animationSpeed),
                title = "侧边栏位置",
                items = SidebarPosition.entries,
                selectedItem = sidebarPosition,
                onValueChange = onPositionChange,
                getItemText = {
                    when (it) {
                        SidebarPosition.Left -> "左侧"
                        SidebarPosition.Right -> "右侧"
                    }
                },
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
        item { 
            SimpleListLayout(
                modifier = Modifier
                    .animatedAppearance(4, animationSpeed),
                title = "主题颜色",
                items = ThemeColor.entries.toList(),
                selectedItem = themeColor,
                onValueChange = { newColor ->
                    if (newColor == ThemeColor.Custom) {
                        tempLightColorScheme = lightColorScheme
                        tempDarkColorScheme = darkColorScheme
                        showColorPickerDialog = true
                    } else {
                        onThemeColorChange(newColor)
                    }
                },
                getItemText = { it.title },
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
        item { 
            SwitchLayout(
                modifier = Modifier
                    .animatedAppearance(5, animationSpeed),
                title = "UI发光效果",
                summary = "为部分文字和图标添加发光效果",
                checked = isGlowEffectEnabled,
                onCheckedChange = { onIsGlowEffectEnabledChange() },
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
        item {
            SwitchLayout(
                modifier = Modifier
                    .animatedAppearance(6, animationSpeed),
                title = "卡片背景启用毛玻璃效果",
                summary = "[Beta](会出现渲染问题)对卡片背景启用毛玻璃效果(Android 12+)",
                checked = isCardBlurEnabled,
                onCheckedChange = { onIsCardBlurEnabledChange() },
                enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
        item { 
            CollapsibleCard(
                modifier = Modifier
                    .animatedAppearance(7, animationSpeed),
                title = "启动器背景",
                summary = "自定义启动器背景",
                animationSpeed = animationSpeed,
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ScalingActionButton(
                            onClick = { imagePicker.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Image,
                            text = "选择图片",
                            animationSpeed = animationSpeed
                        )
                        ScalingActionButton(
                            onClick = { videoPicker.launch("video/*") },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Videocam,
                            text = "选择视频",
                            animationSpeed = animationSpeed
                        )
                    }
                    ScalingActionButton(
                        onClick = {
                            val backgroundFile = File(context.getExternalFilesDir(null), backgroundFileName)
                            if (backgroundFile.exists()) {
                                backgroundFile.delete()
                            }
                            val backgroundVideoFile = File(context.getExternalFilesDir(null), backgroundVideoName)
                            if (backgroundVideoFile.exists()) {
                                backgroundVideoFile.delete()
                            }
                            onLauncherBackgroundUriChange(null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.BrokenImage,
                        text = "清除背景",
                        animationSpeed = animationSpeed
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                SliderLayout(
                    value = launcherBackgroundBlur,
                    onValueChange = onLauncherBackgroundBlurChange,
                    valueRange = 0f..25f,
                    steps = 24,
                    title = "背景模糊",
                    summary = "调整背景图片的模糊程度",
                    displayValue = launcherBackgroundBlur,
                    enabled = launcherBackgroundUri != null && !isBackgroundVideo,
                    isGlowEffectEnabled = isGlowEffectEnabled,
                    isCardBlurEnabled = isCardBlurEnabled,
                    hazeState = hazeState
                )
                Spacer(modifier = Modifier.height(16.dp))
                SliderLayout(
                    value = launcherBackgroundBrightness,
                    onValueChange = onLauncherBackgroundBrightnessChange,
                    valueRange = -100f..100f,
                    steps = 199,
                    title = "背景明度",
                    summary = "调整背景图片的明暗程度(建议还是自己先提前编辑好图片)",
                    displayValue = launcherBackgroundBrightness,
                    enabled = launcherBackgroundUri != null && !isBackgroundVideo,
                    isGlowEffectEnabled = isGlowEffectEnabled,
                    isCardBlurEnabled = isCardBlurEnabled,
                    hazeState = hazeState
                )
                Spacer(modifier = Modifier.height(16.dp))
                SliderLayout(
                    value = launcherBackgroundVideoVolume,
                    onValueChange = onLauncherBackgroundVideoVolumeChange,
                    valueRange = 0f..1f,
                    steps = 19,
                    title = "视频音量",
                    summary = "调整背景视频的音量",
                    displayValue = launcherBackgroundVideoVolume,
                    enabled = isBackgroundVideo,
                    isGlowEffectEnabled = isGlowEffectEnabled,
                    isCardBlurEnabled = isCardBlurEnabled,
                    hazeState = hazeState
                )
            }
        }
        item { 
            CollapsibleCard(
                modifier = Modifier
                    .animatedAppearance(8, animationSpeed),
                title = "背景光效",
                summary = if (enableBackgroundLightEffect) "已开启" else "已关闭",
                animationSpeed = animationSpeed,
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            ) {
                SwitchLayout(
                    checked = enableBackgroundLightEffect,
                    onCheckedChange = { onEnableBackgroundLightEffectChange() },
                    title = "启用背景光效",
                    isCardBlurEnabled = isCardBlurEnabled,
                    hazeState = hazeState
                )
                Spacer(modifier = Modifier.height(16.dp))
                SliderLayout(
                    value = lightEffectAnimationSpeed,
                    onValueChange = onLightEffectAnimationSpeedChange,
                    valueRange = 0.5f..2f,
                    steps = 14,
                    title = "光效运动速度",
                    summary = "控制背景光效的运动速度",
                    displayValue = lightEffectAnimationSpeed,
                    enabled = enableBackgroundLightEffect,
                    isGlowEffectEnabled = isGlowEffectEnabled,
                    isCardBlurEnabled = isCardBlurEnabled,
                    hazeState = hazeState
                )
                Spacer(modifier = Modifier.height(16.dp))
                SwitchLayout(
                    checked = enableBackgroundLightEffectCustomColor,
                    onCheckedChange = { onEnableBackgroundLightEffectCustomColorChange() },
                    title = "自定义光效颜色",
                    enabled = enableBackgroundLightEffect,
                    isCardBlurEnabled = isCardBlurEnabled,
                    hazeState = hazeState
                )
                if (enableBackgroundLightEffectCustomColor) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                tempLightEffectColor = backgroundLightEffectCustomColor
                                showLightEffectColorPickerDialog = true
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("编辑颜色")
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(backgroundLightEffectCustomColor)
                        )
                    }
                }
            }
        }
        item { 
            SliderLayout(
                modifier = Modifier
                    .animatedAppearance(9, animationSpeed),
                value = animationSpeed,
                onValueChange = onAnimationSpeedChange,
                valueRange = 0.5f..2f,
                    steps = 14,
                title = "动画速率",
                summary = "控制 UI 动画的播放速度",
                displayValue = animatedSpeed,
                isGlowEffectEnabled = isGlowEffectEnabled,
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
        item { 
            SliderLayout(
                modifier = Modifier
                    .animatedAppearance(10, animationSpeed),
                value = uiScale,
                onValueChange = onUiScaleChange,
                valueRange = 0.8f..1.5f,
                steps = 13,
                title = "UI 缩放",
                summary = "调整启动器整体界面的大小",
                displayValue = uiScale,
                isGlowEffectEnabled = isGlowEffectEnabled,
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
        item { 
            Spacer(modifier = Modifier.height(45.dp))
        }
    }
}
