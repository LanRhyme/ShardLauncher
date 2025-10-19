package com.lanrhyme.shardlauncher.model

data class Account(
    val id: String,
    val username: String,
    val accountType: AccountType,
    val lastPlayed: String,
    val skinUrl: String
)

enum class AccountType(val displayName: String) {
    ONLINE("微软账户"),
    OFFLINE("离线账户")
}