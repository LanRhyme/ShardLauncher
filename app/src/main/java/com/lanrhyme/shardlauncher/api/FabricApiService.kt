package com.lanrhyme.shardlauncher.api

import com.lanrhyme.shardlauncher.model.FabricLoaderVersion
import retrofit2.http.GET

interface FabricApiService {
    @GET("v2/versions/loader")
    suspend fun getLoaderVersions(): List<FabricLoaderVersion>
}
