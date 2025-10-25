package com.lanrhyme.shardlauncher.utils.network



import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lanrhyme.shardlauncher.R
import com.lanrhyme.shardlauncher.utils.file.compareSHA1
import com.lanrhyme.shardlauncher.utils.file.ensureParentDirectory
import com.lanrhyme.shardlauncher.utils.logging.Logger.lDebug
import com.lanrhyme.shardlauncher.utils.logging.Logger.lWarning
import com.lanrhyme.shardlauncher.utils.string.isEmptyOrBlank
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import okhttp3.Call
import org.apache.commons.io.FileUtils
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit


const val URL_USER_AGENT = "ShardLauncher/1.0"
val TIME_OUT = Pair(10000, TimeUnit.MILLISECONDS)

fun createRequestBuilder(url: String): Request.Builder {
    return createRequestBuilder(url, null)
}

fun createRequestBuilder(url: String, body: RequestBody?): Request.Builder {
    val request = Request.Builder().url(url).header("User-Agent", URL_USER_AGENT)
    body?.let{ request.post(it) }
    return request
}

fun createOkHttpClient(): OkHttpClient = createOkHttpClientBuilder().build()

/**
 * 创建一个OkHttpClient，可自定义一些内容
 */
fun createOkHttpClientBuilder(action: (OkHttpClient.Builder) -> Unit = { }): OkHttpClient.Builder {
    return OkHttpClient.Builder()
        .callTimeout(TIME_OUT.first.toLong(), TIME_OUT.second)
        .apply(action)
}

/**
 * @return 当前网络是否可用
 */
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
    return activeNetwork != null && (
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false ||
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false ||
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ?: false
            )
}

/**
 * 同步下载文件到本地
 * @param url 要下载的文件URL
 * @param outputFile 要保存的目标文件
 * @param bufferSize 缓冲区大小
 * @param sha1 文件SHA1验证值
 * @param sizeCallback 正在下载的大小回调
 */
fun downloadFileWithHttp(
    url: String,
    outputFile: File,
    bufferSize: Int = 65536,
    sha1: String? = null,
    sizeCallback: (Long) -> Unit = {}
) {
    val maxAttempts = if (sha1 != null) 2 else 1
    var attempt = 0
    var totalReportedBytes = 0L

    while (true) {
        attempt++
        //本次尝试中已回调的大小
        var attemptReportedBytes = 0L

        try {
            outputFile.ensureParentDirectory()

            val conn = URL(url).openConnection() as HttpURLConnection

            conn.apply {
                readTimeout = TIME_OUT.first
                connectTimeout = TIME_OUT.first
                useCaches = true
                setRequestProperty("User-Agent", "Mozilla/5.0/$URL_USER_AGENT")
            }

            conn.connect()
            if (conn.responseCode !in 200..299) {
                if (conn.responseCode == 404) throw FileNotFoundException("HTTP ${conn.responseCode} - ${conn.responseMessage}")
                throw IOException("HTTP ${conn.responseCode} - ${conn.responseMessage}")
            }

            val contentLength = conn.contentLengthLong
            val buffer = ByteArray(bufferSize)

            conn.inputStream.use { inputStream ->
                BufferedOutputStream(FileOutputStream(outputFile)).use { fos ->
                    var totalBytesRead = 0L
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        fos.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead

                        sizeCallback(bytesRead.toLong())
                        attemptReportedBytes += bytesRead
                        totalReportedBytes += bytesRead
                    }

                    if (contentLength != -1L && totalBytesRead != contentLength) {
                        throw IOException("Download incomplete. Expected $contentLength bytes, received $totalBytesRead bytes.")
                    }
                }
            }

            sha1?.let {
                if (!compareSHA1(outputFile, it)) {
                    throw IOException("SHA1 verification failed for $url")
                }
            }

            return //下载并验证成功
        } catch (e: Exception) {
            FileUtils.deleteQuietly(outputFile)

            if (attemptReportedBytes > 0) {
                //回退本次尝试的下载量
                sizeCallback(-attemptReportedBytes)
                totalReportedBytes -= attemptReportedBytes
            }

            when (e) {
                is CancellationException -> {
                    lDebug("Download task cancelled. url: $url", e)
                    return //取消了，不需要抛出异常
                }
                is FileNotFoundException -> {
                    if (attempt >= maxAttempts) throw e  //目标不存在
                }
                else -> {
                    if (attempt >= maxAttempts) {
                        throw IOException("Download failed after $maxAttempts attempts: $url", e)
                    }
                }
            }
        }
    }
}

/**
 * 同步下载文件到本地
 * @param url 要下载的文件URL
 * @param outputFile 要保存的目标文件
 * @param bufferSize 缓冲区大小
 * @param sha1 文件SHA1验证值
 * @param sizeCallback 正在下载的大小回调
 */
suspend fun downloadFileSuspend(
    url: String,
    outputFile: File,
    bufferSize: Int = 65536,
    sha1: String? = null,
    sizeCallback: (Long) -> Unit = {}
) = withContext(Dispatchers.IO) {
    runInterruptible {
        downloadFileWithHttp(
            url = url,
            outputFile = outputFile,
            bufferSize = bufferSize,
            sha1 = sha1,
            sizeCallback = sizeCallback
        )
    }
}

/**
 * 从多个下载地址中尝试下载
 * @param urls 要下载的文件链接列表
 * @param outputFile 要保存的目标文件
 * @param bufferSize 缓冲区大小
 * @param sha1 文件SHA1验证值
 * @param sizeCallback 正在下载的大小回调
 */
fun downloadFromMirrorList(
    urls: List<String>,
    outputFile: File,
    bufferSize: Int = 65536,
    sha1: String? = null,
    sizeCallback: (Long) -> Unit = {}
) {
    require(urls.isNotEmpty()) { "URL list must not be empty." }

    val errors = mutableListOf<Exception>()
    var lastException: Exception? = null
    var totalReportedBytes = 0L

    for (url in urls) {
        var attempt = 0
        val maxAttempts = if (sha1 != null) 2 else 1

        while (attempt < maxAttempts) {
            attempt++
            //本次镜像尝试中已回调的大小
            var mirrorAttemptReported = 0L

            try {
                val mirrorCallback = { bytes: Long ->
                    if (bytes > 0) {
                        mirrorAttemptReported += bytes
                        totalReportedBytes += bytes
                    }
                    sizeCallback(bytes)
                }

                downloadFileWithHttp(
                    url = url,
                    outputFile = outputFile,
                    bufferSize = bufferSize,
                    sha1 = sha1,
                    sizeCallback = mirrorCallback
                )
                return //下载成功
            } catch (e: Exception) {
                FileUtils.deleteQuietly(outputFile)
                lastException = e

                if (mirrorAttemptReported > 0) {
                    //回退本次镜像尝试的下载量
                    sizeCallback(-mirrorAttemptReported)
                    totalReportedBytes -= mirrorAttemptReported
                }

                when (e) {
                    is CancellationException -> throw e
                    is FileNotFoundException -> {
                        errors.add(e)
                        break
                    }
                    else -> {
                        errors.add(e)
                    }
                }
            }
        }
    }

    throw IOException("Failed to download file from all mirrors (${errors.size} errors)", lastException).apply {
        errors.forEachIndexed { i, e ->
            addSuppressed(Exception("Mirror error #${i + 1}: ${e.message}"))
        }
    }
}

/**
 * 从多个下载地址中尝试下载
 * @param urls 要下载的文件链接列表
 * @param outputFile 要保存的目标文件
 * @param bufferSize 缓冲区大小
 * @param sha1 文件SHA1验证值
 * @param sizeCallback 正在下载的大小回调
 */
suspend fun downloadFromMirrorListSuspend(
    urls: List<String>,
    outputFile: File,
    bufferSize: Int = 65536,
    sha1: String? = null,
    sizeCallback: (Long) -> Unit = {}
) = withContext(Dispatchers.IO) {
    runInterruptible {
        downloadFromMirrorList(
            urls = urls,
            outputFile = outputFile,
            bufferSize = bufferSize,
            sha1 = sha1,
            sizeCallback = sizeCallback
        )
    }
}

/**
 * 同步获取 URL 返回的字符串内容
 * @param url 要请求的URL地址
 * @return 服务器返回的字符串内容
 * @throws IllegalArgumentException 当URL无效时
 * @throws IOException 当网络请求失败或响应解析失败时
 */
@Throws(IOException::class, IllegalArgumentException::class)
suspend fun fetchStringFromUrl(url: String): String = withContext(Dispatchers.IO) {
    call(url) { call ->
        call.execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code} - ${response.message}")
            }

            return@call response.body?.use { it.string() } ?: throw IOException("Response body is null")
        }
    }
}

/**
 * 同步获取 URL 返回的字符串内容
 * @param urls 要请求的URL源地址
 * @return 服务器返回的字符串内容
 * @throws IllegalArgumentException 当URL无效时
 * @throws IOException 当网络请求失败或响应解析失败时
 */
@OptIn(ExperimentalStdlibApi::class)
@Throws(IOException::class, IllegalArgumentException::class)
suspend fun fetchStringFromUrls(urls: List<String>): String = withContext(Dispatchers.IO) {
    var result: String? = null
    var succeed = false
    var lastException: Throwable? = null

    for (url in urls) {
        runCatching {
            result = fetchStringFromUrl(url)
            succeed = true

        }.onFailure {
            lDebug("Source $url failed!", it)
            lastException = it
        }
    }

    if (!succeed || result == null) throw lastException ?: IOException("Failed to retrieve information from the source!")

    result
}

private fun <T> call(url: String, call: (Call) -> T): T {
    val client = createOkHttpClient()
    val request = createRequestBuilder(url).build()

    return call(client.newCall(request))
}

/**
 * 展示一个提示弹窗，告知用户接下来将要在浏览器内访问的链接，用户可以选择不进行访问
 * @param link 要访问的链接
 */
fun Activity.openLink(link: String) {
    this.openLink(link, null)
}

/**
 * 展示一个提示弹窗，告知用户接下来将要在浏览器内访问的链接，用户可以选择不进行访问
 * @param link 要访问的链接
 * @param dataType 设置 intent 的数据以及显式 MIME 数据类型
 */
fun Activity.openLink(link: String, dataType: String?) {
    if (link.isEmptyOrBlank()) {
        return
    }

    MaterialAlertDialogBuilder(this)
        .setTitle(R.string.generic_open_link)
        .setMessage(link)
        .setPositiveButton(R.string.generic_confirm) { _, _ ->
            try {
                val uri = link.toUri()
                val browserIntent = if (dataType != null) {
                    Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, dataType)
                    }
                } else {
                    Intent(Intent.ACTION_VIEW, uri)
                }
                startActivity(browserIntent)
            } catch (e: Exception) {
                lWarning("Failed to open link: $link", e)
            }
        }
        .setNegativeButton(R.string.generic_cancel) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

suspend fun <T> withRetry(tag: String, maxRetries: Int = 3, delayMillis: Long = 1000, block: suspend () -> T): T {
    var attempts = 0
    while (attempts < maxRetries) {
        try {
            return block()
        } catch (e: Exception) {
            lWarning("[$tag] Attempt ${attempts + 1} failed: ${e.message}", e)
            attempts++
            if (attempts < maxRetries) {
                kotlinx.coroutines.delay(delayMillis)
            } else {
                throw e
            }
        }
    }
    throw IllegalStateException("Should not reach here")
}
