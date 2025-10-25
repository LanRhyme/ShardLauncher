package com.lanrhyme.shardlauncher.model.auth

import com.google.gson.annotations.SerializedName

data class DeviceCodeResponse(
    @SerializedName("user_code") val userCode: String,
    @SerializedName("device_code") val deviceCode: String,
    @SerializedName("verification_uri") val verificationUri: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("interval") val interval: Int,
    @SerializedName("message") val message: String
)