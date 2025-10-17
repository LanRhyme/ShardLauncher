package com.lanrhyme.shardlauncher.model

data class FabricLoaderVersion(
    val separator: String,
    val build: Int,
    val maven: String,
    val version: String,
    val stable: Boolean
)
