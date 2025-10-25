package com.lanrhyme.shardlauncher.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lanrhyme.shardlauncher.utils.logging.Logger

val GSON: Gson = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

inline fun <reified T> String.parseTo(classOfT: Class<T>): T {
    return runCatching {
        GSON.fromJson(this, classOfT)
    }.getOrElse { e ->
        Logger.lError("Failed to parse JSON", e)
        throw e
    }
}