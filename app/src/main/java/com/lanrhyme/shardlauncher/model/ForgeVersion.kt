package com.lanrhyme.shardlauncher.model

data class ForgeVersion(
    val version: String,
    val releaseTime: String? = null,
    val isRecommended: Boolean = false,
    val status: String? = null
)
