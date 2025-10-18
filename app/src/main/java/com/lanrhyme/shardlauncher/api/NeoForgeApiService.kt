package com.lanrhyme.shardlauncher.api

import retrofit2.http.GET
import retrofit2.http.Path

interface NeoForgeApiService {
    @GET("neoforge/list/{mcVersion}")
    suspend fun getNeoForgeVersions(@Path("mcVersion") mcVersion: String): List<String>
}
