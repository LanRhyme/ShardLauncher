package com.lanrhyme.shardlauncher.game.version.download

import com.lanrhyme.shardlauncher.game.addons.mirror.mapMirrorableUrls
import com.lanrhyme.shardlauncher.game.versioninfo.models.GameManifest
import com.lanrhyme.shardlauncher.game.versioninfo.models.OperatingSystem
import com.lanrhyme.shardlauncher.utils.GSON
import com.lanrhyme.shardlauncher.utils.file.compareSHA1
import com.lanrhyme.shardlauncher.utils.logging.Logger.lDebug
import com.lanrhyme.shardlauncher.utils.logging.Logger.lError
import com.lanrhyme.shardlauncher.utils.logging.Logger.lWarning
import com.lanrhyme.shardlauncher.utils.network.fetchStringFromUrls
import com.lanrhyme.shardlauncher.utils.network.withRetry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import java.io.File

private const val UTILS_LOG_TAG = "Minecraft.DownloaderUtils"

suspend fun <T> downloadAndParseJson(
    targetFile: File,
    url: String,
    expectedSHA: String?,
    verifyIntegrity: Boolean,
    classOfT: Class<T>
): T {
    suspend fun downloadAndParse(): T {
        val json = withContext(Dispatchers.IO) {
            val string = withRetry(UTILS_LOG_TAG, maxRetries = 1) {
                fetchStringFromUrls(
                    url.mapMirrorableUrls()
                )
            }
            if (string.isBlank()) {
                lError("Downloaded string is empty, aborting.")
                throw IllegalStateException("Downloaded string is empty.")
            }
            targetFile.writeText(string)
            string
        }
        return GSON.fromJson(json, classOfT)
    }

    if (targetFile.exists()) {
        if (!verifyIntegrity || compareSHA1(targetFile, expectedSHA)) {
            return runCatching {
                GSON.fromJson(targetFile.readText(), classOfT)
            }.getOrElse {
                lWarning("Failed to parse existing JSON, re-downloading...")
                downloadAndParse()
            }
        } else {
            FileUtils.deleteQuietly(targetFile)
        }
    }

    return downloadAndParse()
}

fun artifactToPath(library: GameManifest.Library): String? {
    library.downloads?.artifact?.path?.let { return it }

    val libInfos = library.name.split(":")
    if (libInfos.size < 3) {
        lError("Invalid library name format: ${library.name}")
        return null
    }

    val groupId = libInfos[0].replace('.', '/')
    val artifactId = libInfos[1]
    val version = libInfos[2]

    var classifier = if (libInfos.size > 3) "-${libInfos[3]}" else ""
    if (library.isNative) run {
        val natives = library.natives ?: return@run
        if (natives.isNotEmpty()) {
            //Android，在这里使用Linux
            val native = natives[OperatingSystem.Linux] ?: return@run
            classifier = "-$native"
        }
    }

    return "$groupId/$artifactId/$version/$artifactId-$version$classifier.jar"
}

fun processLibraries(libraries: () -> List<GameManifest.Library>) {
    libraries().forEach { library ->
        val versionSegment = library.name.split(":").getOrNull(2) ?: return
        val versionParts = versionSegment.split(".")

        getLibraryReplacement(library.name, versionParts)?.let { replacement ->
            lDebug("Library ${library.name} has been changed to version ${replacement.newName.split(":").last()}")
            updateLibrary(library, replacement)
        }
    }
}

private fun updateLibrary(
    library: GameManifest.Library,
    replacement: LibraryReplacement
) {
    createLibraryInfo(library)
    library.name = replacement.newName
    library.downloads.artifact.apply {
        path = replacement.newPath
        sha1 = replacement.newSha1
        url = replacement.newUrl
    }
}

private fun createLibraryInfo(library: GameManifest.Library) {
    if (library.downloads?.artifact == null) {
        library.downloads = GameManifest.DownloadsX().apply {
            this.artifact = GameManifest.Artifact()
        }
    }
}