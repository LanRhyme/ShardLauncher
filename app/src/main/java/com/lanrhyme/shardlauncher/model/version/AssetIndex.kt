package com.lanrhyme.shardlauncher.model.version

data class AssetIndex(
    val objects: Map<String, AssetObject>
)

data class AssetObject(
    val hash: String,
    val size: Long
)
