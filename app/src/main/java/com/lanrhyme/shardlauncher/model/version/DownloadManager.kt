package com.lanrhyme.shardlauncher.model.version

import android.content.Context
import com.google.gson.Gson
import com.lanrhyme.shardlauncher.ui.notification.Notification
import com.lanrhyme.shardlauncher.ui.notification.NotificationManager
import com.lanrhyme.shardlauncher.ui.notification.NotificationType
import com.lanrhyme.shardlauncher.utils.network.downloadFromMirrorListSuspend
import com.lanrhyme.shardlauncher.utils.network.fetchStringFromUrls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest
import java.util.UUID

private const val ASSET_BASE_URL = "https://resources.download.minecraft.net/"
private const val LIBRARY_BASE_URL = "https://libraries.minecraft.net/"

data class DownloadRequest(
    val urls: List<String>,
    val targetFile: File,
    val sha1: String?,
    val size: Long
)

class DownloadManager(private val context: Context) {

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState = _downloadState.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val gson = Gson()

    private lateinit var gameDir: File
    private lateinit var librariesDir: File
    private lateinit var assetsDir: File
    private lateinit var assetsIndexesDir: File
    private lateinit var assetsObjectsDir: File

    sealed class DownloadState {
        object Idle : DownloadState()
        data class Downloading(
            val totalSize: Long,
            val downloadedSize: Long,
            val progress: Float
        ) : DownloadState()

        object Finished : DownloadState()
        data class Error(val throwable: Throwable) : DownloadState()
    }

    fun init() {
        gameDir = File(context.filesDir, "game")
        librariesDir = File(gameDir, "libraries")
        assetsDir = File(gameDir, "assets")
        assetsIndexesDir = File(assetsDir, "indexes")
        assetsObjectsDir = File(assetsDir, "objects")
        gameDir.mkdirs()
        librariesDir.mkdirs()
        assetsDir.mkdirs()
        assetsIndexesDir.mkdirs()
        assetsObjectsDir.mkdirs()
    }

    fun startDownload(gameManifest: GameManifest) {
        coroutineScope.launch {
            val notificationId = "download_${UUID.randomUUID()}"
            try {
                NotificationManager.show(
                    Notification(
                        id = notificationId,
                        title = "正在下载 ${gameManifest.id}",
                        message = "准备中...",
                        type = NotificationType.Progress,
                        progress = 0f
                    )
                )

                val downloadPlan = createDownloadPlan(gameManifest)
                val totalSize = downloadPlan.sumOf { it.size }
                var downloadedSize = 0L

                _downloadState.value = DownloadState.Downloading(totalSize, 0, 0f)

                downloadPlan.forEach { request ->
                    if (!verifyFile(request.targetFile, request.sha1)) {
                        downloadFromMirrorListSuspend(request.urls, request.targetFile, request.sha1) { bytesRead ->
                            downloadedSize += bytesRead
                            val progress = if (totalSize > 0) downloadedSize.toFloat() / totalSize else 0f
                            _downloadState.value =
                                DownloadState.Downloading(totalSize, downloadedSize, progress)
                            NotificationManager.updateProgress(notificationId, progress)
                        }
                        if (!verifyFile(request.targetFile, request.sha1)) {
                            request.targetFile.delete()
                            throw SecurityException("File verification failed: ${request.targetFile.path}")
                        }
                    }
                }

                _downloadState.value = DownloadState.Finished
                NotificationManager.show(
                    Notification(
                        id = notificationId,
                        title = "下载完成",
                        message = "${gameManifest.id} 已成功安装",
                        type = NotificationType.Temporary
                    )
                )
            } catch (e: Exception) {
                _downloadState.value = DownloadState.Error(e)
                NotificationManager.show(
                    Notification(
                        id = notificationId,
                        title = "下载失败",
                        message = "下载 ${gameManifest.id} 时发生错误: ${e.message}",
                        type = NotificationType.Error
                    )
                )
            }
        }
    }

    private suspend fun createDownloadPlan(gameManifest: GameManifest): List<DownloadRequest> {
        val requests = mutableListOf<DownloadRequest>()

        // Client JAR
        gameManifest.downloads?.client?.let {
            val versionFolder = File(gameDir, "versions/${gameManifest.id}")
            versionFolder.mkdirs()
            val clientFile = File(versionFolder, "${gameManifest.id}.jar")
            requests.add(DownloadRequest(listOf(it.url!!), clientFile, it.sha1, it.size ?: 0))
        }

        // Assets
        gameManifest.assetIndex?.url?.let { url ->
            val assetIndexJson = fetchStringFromUrls(listOf(url))
            val assetIndex = gson.fromJson(assetIndexJson, com.lanrhyme.shardlauncher.model.version.AssetIndex::class.java)

            val assetIndexFile = File(assetsIndexesDir, "${gameManifest.assetIndex.id}.json")
            assetIndexFile.writeText(assetIndexJson)

            assetIndex.objects.forEach { (_, assetObject) ->
                val hash = assetObject.hash
                val subDir = hash.substring(0, 2)
                val assetFile = File(assetsObjectsDir, "$subDir/$hash")
                assetFile.parentFile?.mkdirs()
                requests.add(
                    DownloadRequest(
                        listOf("$ASSET_BASE_URL$subDir/$hash"),
                        assetFile,
                        hash,
                        assetObject.size
                    )
                )
            }
        }

        // Libraries
        gameManifest.libraries?.forEach { library ->
            val versionParts = gameManifest.id.split(".")
            val replacement = getLibraryReplacement(library.name, versionParts)

            if (replacement != null) {
                val targetFile = File(librariesDir, replacement.newPath)
                targetFile.parentFile?.mkdirs()
                requests.add(
                    DownloadRequest(
                        listOf(replacement.newUrl),
                        targetFile,
                        replacement.newSha1,
                        0 // Size is unknown for replacements
                    )
                )
            } else if (library.isNative()) {
                val nativeKey = library.natives?.get(getOsName())
                library.downloads?.classifiers?.get(nativeKey)?.let {
                    val targetFile = File(librariesDir, it.path!!)
                    targetFile.parentFile?.mkdirs()
                    requests.add(DownloadRequest(listOf(it.url!!), targetFile, it.sha1, it.size ?: 0))
                }
            } else {
                val artifact = library.downloads?.artifact
                val path = artifact?.path ?: libraryNameToPath(library.name)
                if (path.isNotEmpty()) {
                    val url = artifact?.url ?: (LIBRARY_BASE_URL + path)
                    val targetFile = File(librariesDir, path)
                    targetFile.parentFile?.mkdirs()
                    requests.add(
                        DownloadRequest(
                            listOf(url),
                            targetFile,
                            artifact?.sha1,
                            artifact?.size ?: 0
                        )
                    )
                }
            }
        }

        return requests
    }

    private fun libraryNameToPath(name: String): String {
        val parts = name.split(':')
        if (parts.size < 3) return ""
        val group = parts[0].replace('.', '/')
        val artifact = parts[1]
        val version = parts[2]
        val classifier = if (parts.size > 3) "-${parts[3]}" else ""
        return "$group/$artifact/$version/$artifact-$version$classifier.jar"
    }


    private fun verifyFile(file: File, sha1: String?): Boolean {
        if (sha1 == null) return true
        if (!file.exists()) return false

        val digest = MessageDigest.getInstance("SHA-1")
        file.inputStream().use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        val fileSha1 = digest.digest().joinToString("") { "%02x".format(it) }
        return fileSha1.equals(sha1, ignoreCase = true)
    }

    private fun getOsName(): String {
        val os = System.getProperty("os.name", "").toLowerCase()
        return when {
            os.contains("win") -> "windows"
            os.contains("mac") -> "osx"
            os.contains("nix") || os.contains("nux") || os.contains("aix") -> "linux"
            else -> "unknown"
        }
    }

    private fun getLibraryReplacement(libraryName: String, versionParts: List<String>): LibraryReplacement? {
        val major = versionParts.getOrNull(0)?.toIntOrNull() ?: 0
        val minor = versionParts.getOrNull(1)?.toIntOrNull() ?: 0

        return when {
            libraryName.startsWith("net.java.dev.jna:jna:") -> {
                if (major >= 5 && minor >= 13) null
                else LibraryReplacement(
                    newName = "net.java.dev.jna:jna:5.13.0",
                    newPath = "net/java/dev/jna/jna/5.13.0/jna-5.13.0.jar",
                    newSha1 = "1200e7ebeedbe0d10062093f32925a912020e747",
                    newUrl = "https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.13.0/jna-5.13.0.jar"
                )
            }
            libraryName.startsWith("com.github.oshi:oshi-core:") -> {
                if (major != 6 || minor != 2) null
                else LibraryReplacement(
                    newName = "com.github.oshi:oshi-core:6.3.0",
                    newPath = "com/github/oshi/oshi-core/6.3.0/oshi-core-6.3.0.jar",
                    newSha1 = "9e98cf55be371cafdb9c70c35d04ec2a8c2b42ac",
                    newUrl = "https://repo1.maven.org/maven2/com/github/oshi/oshi-core/6.3.0/oshi-core-6.3.0.jar"
                )
            }
            libraryName.startsWith("org.ow2.asm:asm-all:") -> {
                if (major >= 5) null
                else LibraryReplacement(
                    newName = "org.ow2.asm:asm-all:5.0.4",
                    newPath = "org/ow2/asm/asm-all/5.0.4/asm-all-5.0.4.jar",
                    newSha1 = "e6244859997b3d4237a552669279780876228909",
                    newUrl = "https://repo1.maven.org/maven2/org/ow2/asm/asm-all/5.0.4/asm-all-5.0.4.jar"
                )
            }
            else -> null
        }
    }

    data class LibraryReplacement(
        val newName: String,
        val newPath: String,
        val newSha1: String,
        val newUrl: String
    )
}
