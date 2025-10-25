package com.lanrhyme.shardlauncher.model.minecraft

import com.google.gson.annotations.SerializedName

data class MinecraftAuthResponse(
    val username: String,
    val roles: List<String>,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Long
)

data class MinecraftProfile(
    val id: String,
    val name: String,
    val skins: List<Skin>,
    val capes: List<Cape>
) {
    data class Skin(
        val id: String,
        val state: String,
        val url: String,
        val variant: String
    )

    data class Cape(
        val id: String,
        val state: String,
        val url: String,
        val alias: String
    )
}