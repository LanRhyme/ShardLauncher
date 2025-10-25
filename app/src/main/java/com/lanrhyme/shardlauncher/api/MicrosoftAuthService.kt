package com.lanrhyme.shardlauncher.api

import com.lanrhyme.shardlauncher.model.auth.AuthTokenResponse
import com.lanrhyme.shardlauncher.model.auth.DeviceCodeResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MicrosoftAuthService {

    @FormUrlEncoded
    @POST("consumers/oauth2/v2.0/devicecode")
    suspend fun getDeviceCode(
        @Field("client_id") clientId: String,
        @Field("scope") scope: String
    ): DeviceCodeResponse

    @FormUrlEncoded
    @POST("consumers/oauth2/v2.0/token")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("device_code") deviceCode: String
    ): AuthTokenResponse
}