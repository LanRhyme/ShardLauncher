package com.lanrhyme.shardlauncher.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.lanrhyme.shardlauncher.data.SettingsRepository
import com.lanrhyme.shardlauncher.setting.enums.MirrorSourceType

class SettingsProvider(private val settingsRepository: SettingsRepository) {
    val fileDownloadSource: MirrorSourceType
        get() = if (settingsRepository.getUseBmclapi()) MirrorSourceType.MIRROR_FIRST else MirrorSourceType.OFFICIAL_FIRST
}

val LocalSettingsProvider = staticCompositionLocalOf<SettingsProvider> { error("No SettingsProvider provided") }