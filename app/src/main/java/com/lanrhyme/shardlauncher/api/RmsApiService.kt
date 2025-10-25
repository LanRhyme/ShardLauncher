package com.lanrhyme.shardlauncher.api

import retrofit2.Response
import retrofit2.http.HEAD
import retrofit2.http.Path

interface RmsApiService {
    @HEAD("head/{username}")
    suspend fun checkHead(@Path("username") username: String): Response<Void>
}
