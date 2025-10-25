package com.lanrhyme.shardlauncher.game.versioninfo.models

/**
 * 简易的版本类型过滤器，过滤版本：正式版、快照版、远古版
 * @param release 是否保留正式版
 * @param snapshot 是否保留快照版
 * @param old 是否保留远古版
 */
fun List<VersionManifest.Version>.filterType(
    release: Boolean,
    snapshot: Boolean,
    old: Boolean
) = this.filter { version ->
    version.isType(release, snapshot, old)
}


/**
 * 检查版本类型是否匹配给定的类型
 * @param release 如果该版本为正式版，则返回它的值
 * @param snapshot 如果该版本为快照版，则返回它的值
 * @param old 如果该版本为远古版，则返回它的值
 */
fun VersionManifest.Version.isType(
    release: Boolean,
    snapshot: Boolean,
    old: Boolean
) = when (type) {
    "release" -> release
    "snapshot", "pending" -> snapshot
    else -> old && type.startsWith("old")
}