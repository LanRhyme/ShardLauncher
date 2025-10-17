package com.lanrhyme.shardlauncher.ui

import androidx.compose.runtime.compositionLocalOf
import com.lanrhyme.shardlauncher.data.SettingsRepository

val LocalSettings = compositionLocalOf<SettingsRepository> {
    error("No SettingsRepository provided")
}
