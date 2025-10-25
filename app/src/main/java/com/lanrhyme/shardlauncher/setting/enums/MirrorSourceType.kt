package com.lanrhyme.shardlauncher.setting.enums

import com.lanrhyme.shardlauncher.R

enum class MirrorSourceType(val textRes: Int) {
    /**
     * 官方源优先
     */
    OFFICIAL_FIRST(R.string.settings_launcher_mirror_official_first),

    /**
     * 镜像源优先
     */
    MIRROR_FIRST(R.string.settings_launcher_mirror_mirror_first)
}