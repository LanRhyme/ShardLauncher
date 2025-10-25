package com.lanrhyme.shardlauncher.api

import com.google.gson.JsonObject
import com.lanrhyme.shardlauncher.model.minecraft.MinecraftAuthResponse
import com.lanrhyme.shardlauncher.model.minecraft.MinecraftProfile
import com.lanrhyme.shardlauncher.model.minecraft.XblAuthResponse
import com.lanrhyme.shardlauncher.model.minecraft.XstsAuthResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MinecraftAuthService {

    @POST("https://user.auth.xbox.com/user/authenticate")
    suspend fun authXbl(@Body body: JsonObject): XblAuthResponse

    @POST("https://xsts.auth.xbox.com/xsts/authorize")
    suspend fun authXsts(@Body body: JsonObject): XstsAuthResponse

    @POST("https://api.minecraftservices.com/authentication/login_with_xbox")
    suspend fun loginWithMinecraft(@Body body: JsonObject): MinecraftAuthResponse

    @GET("https://api.minecraftservices.com/minecraft/profile")
    suspend fun getMinecraftProfile(@Header("Authorization") auth: String): MinecraftProfile
}