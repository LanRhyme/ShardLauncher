package com.lanrhyme.shardlauncher.model

data class LoaderVersion(
    val version: String,
    val status: String? = null,
    val releaseTime: String? = null,
    val isRecommended: Boolean = false
)
