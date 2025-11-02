package com.lanrhyme.shardlauncher.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.common.SidebarPosition
import com.lanrhyme.shardlauncher.ui.components.CollapsibleCard
import com.lanrhyme.shardlauncher.ui.components.ScalingActionButton
import com.lanrhyme.shardlauncher.ui.components.SimpleListLayout
import com.lanrhyme.shardlauncher.ui.components.SliderLayout
import com.lanrhyme.shardlauncher.ui.components.SwitchLayout
import com.lanrhyme.shardlauncher.ui.theme.ThemeColor
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.animatedAppearance(index: Int, animationSpeed: Float): Modifier = composed {
    var animated by remember { mutableStateOf(false) }
    val animationDuration = (300 / animationSpeed).toInt()
    val delay = (60 * index / animationSpeed).toInt()

    val alpha by animateFloatAsState(
        targetValue = if (animated) 1f else 0f,
        animationSpec = tween(durationMillis = animationDuration, delayMillis = delay),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (animated) 1f else 0.95f,
        animationSpec = tween(durationMillis = animationDuration, delayMillis = delay),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        animated = true
    }

    this.graphicsLayer(
        alpha = alpha,
        scaleX = scale,
        scaleY = scale
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LauncherSettingsContent(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    sidebarPosition: SidebarPosition,
    onPositionChange: (SidebarPosition) -> Unit,
    themeColor: ThemeColor,
    onThemeColorChange: (ThemeColor) -> Unit,
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
    val animatedSpeed by animateFloatAsState(
        targetValue = animationSpeed,
        label = "Animation Speed",
        animationSpec = tween((1000 / animationSpeed).toInt())
    )
    val context = LocalContext.current
    val backgroundFileName = "launcher_background.jpg"
    val backgroundVideoName = "launcher_background.mp4"

    val isBackgroundVideo = launcherBackgroundUri?.endsWith(".mp4") == true

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
                onCheckedChange = onThemeToggle
            )
        }
        item { 
            SwitchLayout(
                modifier = Modifier
                    .animatedAppearance(2, animationSpeed),
                title = "获取Minecraft最新更新信息",
                summary = "来源于news.bugjump.net",
                checked = enableVersionCheck,
                onCheckedChange = { onEnableVersionCheckChange() }
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
                }
            )
        }
        item { 
            SimpleListLayout(
                modifier = Modifier
                    .animatedAppearance(4, animationSpeed),
                title = "主题颜色",
                items = ThemeColor.entries.toList(),
                selectedItem = themeColor,
                onValueChange = onThemeColorChange,
                getItemText = { it.title }
            )
        }
        item { 
            SwitchLayout(
                modifier = Modifier
                    .animatedAppearance(5, animationSpeed),
                title = "UI发光效果",
                summary = "为部分文字和图标添加发光效果",
                checked = isGlowEffectEnabled,
                onCheckedChange = { onIsGlowEffectEnabledChange() }
            )
        }
        item { 
            CollapsibleCard(
                modifier = Modifier
                    .animatedAppearance(6, animationSpeed),
                title = "启动器背景",
                summary = "自定义启动器背景",
                animationSpeed = animationSpeed
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
                    isGlowEffectEnabled = isGlowEffectEnabled
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
                    isGlowEffectEnabled = isGlowEffectEnabled
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
                    isGlowEffectEnabled = isGlowEffectEnabled
                )
            }
        }
        item { 
            CollapsibleCard(
                modifier = Modifier
                    .animatedAppearance(7, animationSpeed),
                title = "背景光效",
                summary = if (enableBackgroundLightEffect) "已开启" else "已关闭",
                animationSpeed = animationSpeed
            ) {
                SwitchLayout(
                    checked = enableBackgroundLightEffect,
                    onCheckedChange = { onEnableBackgroundLightEffectChange() },
                    title = "启用背景光效"
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
                    isGlowEffectEnabled = isGlowEffectEnabled
                )
            }
        }
        item { 
            SliderLayout(
                modifier = Modifier
                    .animatedAppearance(8, animationSpeed),
                value = animationSpeed,
                onValueChange = onAnimationSpeedChange,
                valueRange = 0.5f..2f,
                steps = 14,
                title = "动画速率",
                summary = "控制 UI 动画的播放速度",
                displayValue = animatedSpeed,
                isGlowEffectEnabled = isGlowEffectEnabled
            )
        }
        item { 
            SliderLayout(
                modifier = Modifier
                    .animatedAppearance(9, animationSpeed),
                value = uiScale,
                onValueChange = onUiScaleChange,
                valueRange = 0.8f..1.5f,
                steps = 13,
                title = "UI 缩放",
                summary = "调整启动器整体界面的大小",
                displayValue = uiScale,
                isGlowEffectEnabled = isGlowEffectEnabled
            )
        }
        item { 
            Spacer(modifier = Modifier.height(45.dp))
        }
    }
}
