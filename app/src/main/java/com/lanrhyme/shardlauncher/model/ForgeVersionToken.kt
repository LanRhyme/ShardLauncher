package com.lanrhyme.shardlauncher.model

data class ForgeVersionToken(
    val branch: String?,
    val version: String,
    val modified: String,
    val files: List<ForgeFile>
) {
    data class ForgeFile(
        val category: String,
        val format: String,
        val hash: String
    )
}
