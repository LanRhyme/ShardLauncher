package com.lanrhyme.shardlauncher.api

import com.lanrhyme.shardlauncher.model.ForgeVersionToken
import retrofit2.http.GET
import retrofit2.http.Path

interface ForgeApiService {
    @GET("forge/minecraft/{mcVersion}")
    suspend fun getForgeVersions(@Path("mcVersion") mcVersion: String): List<ForgeVersionToken>
}
