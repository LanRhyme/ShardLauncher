package com.lanrhyme.shardlauncher.api

import com.lanrhyme.shardlauncher.model.LatestVersionsResponse
import retrofit2.http.GET

interface VersionApiService {
    @GET("apis/versions/latest")
    suspend fun getLatestVersions(): LatestVersionsResponse
}
