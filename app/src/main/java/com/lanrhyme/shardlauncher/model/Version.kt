package com.lanrhyme.shardlauncher.model

import com.google.gson.annotations.SerializedName

data class LatestVersionsResponse(
    val snapshot: VersionInfo?,
    val release: VersionInfo
)

data class VersionInfo(
    @SerializedName("version-type")
    val versionType: String,
    val intro: String?,
    @SerializedName("version-image-link")
    val versionImageLink: String,
    @SerializedName("server-jar")
    val serverJar: String,
    val translator: String?,
    @SerializedName("official-link")
    val officialLink: String,
    @SerializedName("wiki-link")
    val wikiLink: String,
    @SerializedName("version-id")
    val versionId: String,
    val title: String,
    @SerializedName("homepage-json-link")
    val homepageJsonLink: String
)
