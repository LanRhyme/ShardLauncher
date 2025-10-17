package com.lanrhyme.shardlauncher.api

import com.lanrhyme.shardlauncher.model.BmclapiManifest
import retrofit2.http.GET

interface BmclapiService {
    @GET("mc/game/version_manifest.json")
    suspend fun getGameVersionManifest(): BmclapiManifest
}
