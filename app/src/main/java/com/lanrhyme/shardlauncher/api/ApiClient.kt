package com.lanrhyme.shardlauncher.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val NEWS_BASE_URL = "https://news.bugjump.net/"
    private const val BMCLAPI_BASE_URL = "https://bmclapi2.bangbang93.com/"
    private const val FABRIC_BASE_URL = "https://meta.fabricmc.net/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val versionApiService: VersionApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(NEWS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(VersionApiService::class.java)
    }

    val bmclapiService: BmclapiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BMCLAPI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(BmclapiService::class.java)
    }

    val fabricApiService: FabricApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(FABRIC_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(FabricApiService::class.java)
    }
}
