package com.lanrhyme.shardlauncher.game.versioninfo

import com.lanrhyme.shardlauncher.R
import java.util.regex.Pattern

/**
 * 所有支持的资源下载过滤的版本号
 */
val allGameVersions = listOf(
    "1.21.10", "1.21.9", "1.21.8", "1.21.7", "1.21.6", "1.21.5", "1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21",
    "1.20.6", "1.20.5", "1.20.4", "1.20.3", "1.20.2", "1.20.1", "1.20",
    "1.19.4", "1.19.3", "1.19.2", "1.19.1", "1.19",
    "1.18.2", "1.18.1", "1.18",
    "1.17.1", "1.17",
    "1.16.5", "1.16.4", "1.16.3", "1.16.2", "1.16.1", "1.16",
    "1.15.2", "1.15.1", "1.15",
    "1.14.4", "1.14.3", "1.14.2", "1.14.1", "1.14",
    "1.13.2", "1.13.1", "1.13",
    "1.12.2", "1.12.1", "1.12",
    "1.11.2", "1.11.1", "1.11",
    "1.10.2", "1.10.1", "1.10",
    "1.9.4", "1.9.3", "1.9.2", "1.9.1", "1.9",
    "1.8.9", "1.8.8", "1.8.7", "1.8.6", "1.8.5", "1.8.4", "1.8.3", "1.8.2", "1.8.1", "1.8",
    "1.7.10", "1.7.9", "1.7.8", "1.7.7", "1.7.6", "1.7.5", "1.7.4", "1.7.3", "1.7.2",
    "1.6.4", "1.6.2", "1.6.1",
    "1.5.2", "1.5.1",
    "1.4.7", "1.4.6", "1.4.5", "1.4.4", "1.4.2",
    "1.3.2", "1.3.1",
    "1.2.5", "1.2.4", "1.2.3", "1.2.2", "1.2.1",
    "1.1",
    "1.0"
)

/**
 * 愚人节版本类型
 */
enum class AprilFoolsType(
    val summary: Int? = null
) {
    /**
     * [Wiki](https://zh.minecraft.wiki/w/25w14craftmine)
     */
    CraftMine(R.string.version_summary_fools_creaft_mine),
    /**
     * [Wiki](https://zh.minecraft.wiki/w/24w14potato)
     */
    Potato(R.string.version_summary_fools_potato),
    /**
     * [Wiki](https://zh.minecraft.wiki/w/23w13a_or_b)
     */
    AOrB(R.string.version_summary_fools_a_or_b),
    /**
     * [Wiki](https://zh.minecraft.wiki/w/22w13oneBlockAtATime)
     */
    OneBlockAtATime(R.string.version_summary_fools_one_block_at_time),
    /**
     * [Wiki](https://zh.minecraft.wiki/w/20w14infinite)
     */
    Infinite(R.string.version_summary_fools_infinite),
    /**
     * [Wiki](https://zh.minecraft.wiki/w/Java%E7%89%883D_Shareware%E7%89%88v1.34?variant=zh-cn)
     */
    Minecraft3DShareware(R.string.version_summary_fools_3d_shareware),
    /**
     * [Wiki](https://zh.minecraft.wiki/w/Java%E7%89%881.RV-Pre1?variant=zh-cn)
     */
    TrendyUpdate(R.string.version_summary_fools_trendy_update),
    /**
     * [Wiki](https://zh.minecraft.wiki/w/15w14a)
     */
    TheLoveAndHugsUpdate(R.string.version_summary_fools_the_love_and_hugs_update)
}

/**
 * 愚人节版本
 */
data class AprilFoolsVersion(
    val version: String,
    val type: AprilFoolsType
)

/**
 * 可供下载的愚人节版本
 */
val allAprilFools = listOf(
    AprilFoolsVersion("25w14craftmine", AprilFoolsType.CraftMine),
    AprilFoolsVersion("24w14potato", AprilFoolsType.Potato),
    AprilFoolsVersion("23w13a_or_b", AprilFoolsType.AOrB),
    AprilFoolsVersion("22w13oneblockatatime", AprilFoolsType.OneBlockAtATime),
    AprilFoolsVersion("20w14infinite", AprilFoolsType.Infinite),
    AprilFoolsVersion("20w14∞", AprilFoolsType.Infinite),
    AprilFoolsVersion("3D Shareware v1.34", AprilFoolsType.Minecraft3DShareware),
    AprilFoolsVersion("1.RV-Pre1", AprilFoolsType.TrendyUpdate),
    AprilFoolsVersion("15w14a", AprilFoolsType.TheLoveAndHugsUpdate)
)

/**
 * MC 正式版版本匹配
 */
val RELEASE_REGEX = Pattern.compile("^\\d+\\.\\d+\\.\\d+$" + "|^\\d+\\.\\d+$")
