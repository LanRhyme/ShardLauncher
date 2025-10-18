package com.lanrhyme.shardlauncher.api

import com.lanrhyme.shardlauncher.model.OptiFineVersionToken
import retrofit2.http.GET

interface OptiFineApiService {
    @GET("optifine/versionList")
    suspend fun getOptiFineVersions(): List<OptiFineVersionToken>
}
