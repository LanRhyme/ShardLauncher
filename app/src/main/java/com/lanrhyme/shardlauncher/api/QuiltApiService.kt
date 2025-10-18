package com.lanrhyme.shardlauncher.api

import com.lanrhyme.shardlauncher.model.QuiltVersion
import retrofit2.http.GET
import retrofit2.http.Path

interface QuiltApiService {
    @GET("v3/versions/loader/{mcVersion}")
    suspend fun getQuiltVersions(@Path("mcVersion") mcVersion: String): List<QuiltVersion>
}
