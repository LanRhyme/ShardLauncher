package com.lanrhyme.shardlauncher.model.auth

import com.google.gson.annotations.SerializedName

data class AuthTokenResponse(
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("scope") val scope: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("error") val error: String?,
    @SerializedName("error_description") val errorDescription: String?
)