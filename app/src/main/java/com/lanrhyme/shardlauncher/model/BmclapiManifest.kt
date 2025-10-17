package com.lanrhyme.shardlauncher.model

data class BmclapiManifest(
    val latest: Latest,
    val versions: List<Version>
) {
    data class Latest(
        val release: String,
        val snapshot: String
    )

    data class Version(
        val id: String,
        val type: String,
        val url: String,
        val time: String,
        val releaseTime: String
    )
}
