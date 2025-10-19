package com.lanrhyme.shardlauncher.utils.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private val client = OkHttpClient()

suspend fun fetchStringFromUrls(urls: List<String>): String = withContext(Dispatchers.IO) {
    var lastException: Throwable? = null
    for (url in urls) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()?.let { return@withContext it }
                }
            }
        } catch (e: IOException) {
            lastException = e
        }
    }
    throw IOException("Failed to fetch string from all URLs", lastException)
}

suspend fun downloadFromMirrorListSuspend(
    urls: List<String>,
    outputFile: File,
    sha1: String? = null,
    chunkCallback: (bytesRead: Long) -> Unit = {}
) = withContext(Dispatchers.IO) {
    var lastException: Throwable? = null
    for (url in urls) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.byteStream()?.use { input ->
                        FileOutputStream(outputFile).use { output ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                chunkCallback(bytesRead.toLong())
                            }
                        }
                        return@withContext
                    }
                }
            }
        } catch (e: IOException) {
            lastException = e
            outputFile.delete()
        }
    }
    throw IOException("Failed to download file from all mirrors", lastException)
}
