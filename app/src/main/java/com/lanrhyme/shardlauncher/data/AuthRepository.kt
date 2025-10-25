package com.lanrhyme.shardlauncher.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lanrhyme.shardlauncher.BuildConfig
import com.lanrhyme.shardlauncher.api.MicrosoftAuthService
import com.lanrhyme.shardlauncher.api.MinecraftAuthService
import com.lanrhyme.shardlauncher.api.MojangApiService
import com.lanrhyme.shardlauncher.api.RmsApiService
import com.lanrhyme.shardlauncher.model.auth.AuthTokenResponse
import com.lanrhyme.shardlauncher.model.auth.DeviceCodeResponse
import com.lanrhyme.shardlauncher.model.minecraft.MinecraftAuthResponse
import com.lanrhyme.shardlauncher.model.minecraft.MinecraftProfile
import com.lanrhyme.shardlauncher.model.mojang.MojangProfile
import kotlinx.coroutines.delay

class AuthRepository(
    private val microsoftAuthService: MicrosoftAuthService,
    private val minecraftAuthService: MinecraftAuthService,
    private val mojangApiService: MojangApiService,
    private val rmsApiService: RmsApiService
) {

    private val gson = Gson()

    suspend fun getDeviceCode(): DeviceCodeResponse {
        return microsoftAuthService.getDeviceCode(
            BuildConfig.CLIENT_ID,
            "XboxLive.signin offline_access"
        )
    }

    suspend fun pollForToken(deviceCodeResponse: DeviceCodeResponse): AuthTokenResponse {
        while (true) {
            try {
                return microsoftAuthService.getAccessToken(
                    BuildConfig.CLIENT_ID,
                    "urn:ietf:params:oauth:grant-type:device_code",
                    deviceCodeResponse.deviceCode
                )
            } catch (e: Exception) {
                val error = gson.fromJson(e.message, AuthTokenResponse::class.java)
                when (error.error) {
                    "authorization_pending" -> {
                        delay(deviceCodeResponse.interval * 1000L)
                    }
                    "slow_down" -> {
                        delay((deviceCodeResponse.interval + 5) * 1000L)
                    }
                    else -> throw e
                }
            }
        }
    }

    suspend fun getMinecraftAuth(accessToken: String): MinecraftAuthResponse {
        val xblToken = minecraftAuthService.authXbl(JsonObject().apply {
            addProperty("RelyingParty", "http://auth.xboxlive.com")
            addProperty("TokenType", "JWT")
            add("Properties", JsonObject().apply {
                addProperty("AuthMethod", "RPS")
                addProperty("SiteName", "user.auth.xbox.com")
                addProperty("RpsTicket", "d=$accessToken")
            })
        })

        val xstsToken = minecraftAuthService.authXsts(JsonObject().apply {
            addProperty("RelyingParty", "rp://api.minecraftservices.com/")
            addProperty("TokenType", "JWT")
            add("Properties", JsonObject().apply {
                add("UserTokens", gson.toJsonTree(listOf(xblToken.token)))
            })
        })

        return minecraftAuthService.loginWithMinecraft(JsonObject().apply {
            addProperty("identityToken", "XBL3.0 x=${xblToken.displayClaims.xui.first().userHash};${xstsToken.token}")
        })
    }

    suspend fun getMinecraftProfile(accessToken: String): MinecraftProfile {
        return minecraftAuthService.getMinecraftProfile("Bearer $accessToken")
    }

    suspend fun getOfflineSkinUrl(username: String): String {
        return try {
            val response = rmsApiService.checkHead(username)
            if (response.isSuccessful) {
                "http://api.rms.net.cn/head/$username"
            } else {
                getMojangSkinUrl(username)
            }
        } catch (e: Exception) {
            getMojangSkinUrl(username)
        }
    }

    private suspend fun getMojangSkinUrl(username: String): String {
        return try {
            val mojangProfile = mojangApiService.getProfile(username)
            "https://crafatar.com/avatars/${mojangProfile.id}"
        } catch (e: Exception) {
            "https://crafatar.com/avatars/8667ba71-b85a-4004-af54-457a9734eed7" // Default Steve skin
        }
    }
}
