package com.lanrhyme.shardlauncher.model

import com.google.gson.annotations.SerializedName

data class OptiFineVersionToken(
    @SerializedName("mcversion")
    val mcVersion: String,
    val type: String,
    val patch: String,
    @SerializedName("filename")
    val fileName: String,
    val forge: String? = null
)
