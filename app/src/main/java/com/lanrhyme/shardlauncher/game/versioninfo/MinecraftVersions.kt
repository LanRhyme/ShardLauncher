package com.lanrhyme.shardlauncher.game.versioninfo

import com.lanrhyme.shardlauncher.game.versioninfo.models.VersionManifest
import com.lanrhyme.shardlauncher.game.versioninfo.models.filterType
import com.lanrhyme.shardlauncher.path.PathManager
import com.lanrhyme.shardlauncher.path.URL_MINECRAFT_VERSION_REPOS
import com.lanrhyme.shardlauncher.utils.GSON
import com.lanrhyme.shardlauncher.utils.logging.Logger.lWarning
import com.lanrhyme.shardlauncher.utils.network.fetchStringFromUrl
import com.lanrhyme.shardlauncher.utils.network.withRetry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import java.util.concurrent.TimeUnit

object MinecraftVersions {
    private var manifest: VersionManifest? = null

    private val _releasesFlow = MutableStateFlow<List<String>?>(null)
    val releasesFlow: StateFlow<List<String>?> = _releasesFlow

    /**
     * 刷新Minecraft正式版本的版本号列表
     * @param force 强制下载更新版本列表
     */
    @Throws(IllegalStateException::class)
    suspend fun refreshReleaseVersions(force: Boolean = false) {
        if (!force && _releasesFlow.value != null) return

        val vm = if (force) {
            getVersionManifest(force = true)
        } else {
            manifest ?: getVersionManifest(force = false)
        }
        refreshReleaseVersions(vm)
    }

    /**
     * 获取Minecraft版本信息列表
     * @param force 强制下载更新版本列表
     */
    @Throws(IllegalStateException::class)
    suspend fun getVersionManifest(force: Boolean = false): VersionManifest {
        manifest?.takeIf { !force }?.let { return it }

        return withContext(Dispatchers.IO) {
            val localManifestFile = PathManager.FILE_MINECRAFT_VERSIONS
            val isOutdated = !localManifestFile.exists() || !localManifestFile.isFile ||
                    //一天更新一次版本信息列表
                    localManifestFile.lastModified() + TimeUnit.DAYS.toMillis(1) < System.currentTimeMillis()

            val newManifest = if (force || isOutdated) {
                downloadVersionManifest()
            } else {
                try {
                    GSON.fromJson(localManifestFile.readText(), VersionManifest::class.java)
                } catch (e: Exception) {
                    lWarning("Failed to parse version manifest, will redownload", e)
                    //读取失败则删除当前的版本信息文件
                    FileUtils.deleteQuietly(localManifestFile)
                    downloadVersionManifest()
                }
            }

            newManifest ?: throw IllegalStateException("Version manifest is null after all attempts")
        }.also { newManifest ->
            manifest = newManifest
        }
    }

    /**
     * 根据版本清单刷新当前的正式版本列表
     */
    private fun refreshReleaseVersions(vm: VersionManifest) {
        val releases = vm.versions.filterType(
            release = true,
            snapshot = false,
            old = false
        )
        _releasesFlow.update {
            releases.map { version -> version.id }
        }
    }

    /**
     * 从官方版本仓库获取版本信息
     */
    private suspend fun downloadVersionManifest(): VersionManifest {
        return withContext(Dispatchers.IO) {
            withRetry("MinecraftVersions", maxRetries = 1) {
                val rawJson = fetchStringFromUrl(URL_MINECRAFT_VERSION_REPOS)
                val versionManifest = GSON.fromJson(rawJson, VersionManifest::class.java)
                PathManager.FILE_MINECRAFT_VERSIONS.writeText(rawJson)
                versionManifest
            }
        }
    }
}