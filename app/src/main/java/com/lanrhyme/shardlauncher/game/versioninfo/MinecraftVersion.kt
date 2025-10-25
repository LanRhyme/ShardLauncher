package com.lanrhyme.shardlauncher.game.versioninfo

import com.lanrhyme.shardlauncher.game.versioninfo.models.VersionManifest

/**
 * Minecraft version.
 * @param version The version string.
 * @param type The type of the version.
 * @param summary The summary of the version.
 */
class MinecraftVersion(
    val version: VersionManifest.Version,
    val type: Type,
    val summary: Int?
): Comparable<MinecraftVersion> {
    override fun compareTo(other: MinecraftVersion): Int {
        return version.releaseTime.compareTo(other.version.releaseTime)
    }

    enum class Type {
        /**
         * 正式版
         */
        Release,

        /**
         * 快照版
         */
        Snapshot,

        /**
         * 远古Beta版
         */
        OldBeta,

        /**
         * 远古Alpha版
         */
        OldAlpha,

        /**
         * 愚人节版
         */
        AprilFools,

        Unknown
    }
}