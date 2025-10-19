package com.lanrhyme.shardlauncher.model.version

import com.google.gson.annotations.SerializedName

data class VersionManifest(
    val latest: Latest,
    val versions: List<Version>
)

data class Latest(
    val release: String,
    val snapshot: String
)

data class Version(
    val id: String,
    val type: String, // "release", "snapshot", "old_beta", "old_alpha"
    val url: String, // URL to the version-specific JSON
    val time: String,
    val releaseTime: String,
    @SerializedName("sha1") val sha1: String,
    val complianceLevel: Int
)
