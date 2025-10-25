package com.lanrhyme.shardlauncher.model.minecraft

import com.google.gson.annotations.SerializedName

data class XblAuthResponse(
    @SerializedName("Token") val token: String,
    @SerializedName("DisplayClaims") val displayClaims: DisplayClaims
) {
    data class DisplayClaims(
        @SerializedName("xui") val xui: List<XuiClaim>
    ) {
        data class XuiClaim(
            @SerializedName("uhs") val userHash: String
        )
    }
}

typealias XstsAuthResponse = XblAuthResponse
