package com.lanrhyme.shardlauncher.ui.downloads

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lanrhyme.shardlauncher.api.ApiClient
import com.lanrhyme.shardlauncher.game.version.download.BaseMinecraftDownloader
import com.lanrhyme.shardlauncher.game.version.download.DownloadMode
import com.lanrhyme.shardlauncher.game.version.download.MinecraftDownloader
import com.lanrhyme.shardlauncher.model.FabricLoaderVersion
import com.lanrhyme.shardlauncher.model.LoaderVersion
import com.lanrhyme.shardlauncher.model.ForgeVersionToken
import com.lanrhyme.shardlauncher.model.QuiltVersion
import com.lanrhyme.shardlauncher.model.OptiFineVersionToken
import com.lanrhyme.shardlauncher.model.version.VersionManager
import com.lanrhyme.shardlauncher.coroutine.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ModLoader {
    None,
    Forge,
    Fabric,
    NeoForge,
    Quilt
}

class VersionDetailViewModel(application: Application, private val versionId: String) : AndroidViewModel(application) {

    private val _versionName = MutableStateFlow(versionId)
    val versionName = _versionName.asStateFlow()
    private var isVersionNameManuallyEdited = false

    // Mod Loader State
    private val _selectedModLoader = MutableStateFlow(ModLoader.None)
    val selectedModLoader = _selectedModLoader.asStateFlow()

    private val _isFabricApiSelected = MutableStateFlow(false)
    val isFabricApiSelected = _isFabricApiSelected.asStateFlow()
    private val _fabricApiVersions = MutableStateFlow<List<String>>(emptyList()) // Placeholder
    val fabricApiVersions = _fabricApiVersions.asStateFlow()
    private val _selectedFabricApiVersion = MutableStateFlow<String?>(null)
    val selectedFabricApiVersion = _selectedFabricApiVersion.asStateFlow()

    private val _fabricVersions = MutableStateFlow<List<FabricLoaderVersion>>(emptyList())
    val fabricVersions = _fabricVersions.asStateFlow()
    private val _selectedFabricVersion = MutableStateFlow<FabricLoaderVersion?>(null)
    val selectedFabricVersion = _selectedFabricVersion.asStateFlow()

    private val _forgeVersions = MutableStateFlow<List<LoaderVersion>>(emptyList())
    val forgeVersions = _forgeVersions.asStateFlow()
    private val _selectedForgeVersion = MutableStateFlow<LoaderVersion?>(null)
    val selectedForgeVersion = _selectedForgeVersion.asStateFlow()

    private val _neoForgeVersions = MutableStateFlow<List<LoaderVersion>>(emptyList())
    val neoForgeVersions = _neoForgeVersions.asStateFlow()
    private val _selectedNeoForgeVersion = MutableStateFlow<LoaderVersion?>(null)
    val selectedNeoForgeVersion = _selectedNeoForgeVersion.asStateFlow()

    private val _quiltVersions = MutableStateFlow<List<LoaderVersion>>(emptyList())
    val quiltVersions = _quiltVersions.asStateFlow()
    private val _selectedQuiltVersion = MutableStateFlow<LoaderVersion?>(null)
    val selectedQuiltVersion = _selectedQuiltVersion.asStateFlow()

    // Optifine State
    private val _isOptifineSelected = MutableStateFlow(false)
    val isOptifineSelected = _isOptifineSelected.asStateFlow()

    private val _optifineVersions = MutableStateFlow<List<LoaderVersion>>(emptyList())
    val optifineVersions = _optifineVersions.asStateFlow()
    private val _selectedOptifineVersion = MutableStateFlow<LoaderVersion?>(null)
    val selectedOptifineVersion = _selectedOptifineVersion.asStateFlow()

    private val _downloadTask = MutableStateFlow<Task?>(null)
    val downloadTask = _downloadTask.asStateFlow()

    init {
        VersionManager.init(application)
        loadAllLoaderVersions()
    }

    private fun loadAllLoaderVersions() {
        viewModelScope.launch {
            try {
                val fabricVersions = ApiClient.fabricApiService.getLoaderVersions(versionId)
                _fabricVersions.value = fabricVersions
                _selectedFabricVersion.value = fabricVersions.firstOrNull { it.stable == true }

                val forgeVersionTokens = ApiClient.forgeApiService.getForgeVersions(versionId)
                val forgeVersions = forgeVersionTokens.map { it.toLoaderVersion() }
                _forgeVersions.value = forgeVersions
                _selectedForgeVersion.value = forgeVersions.firstOrNull { it.isRecommended }

                val neoForgeVersionStrings = ApiClient.neoForgeApiService.getNeoForgeVersions(versionId)
                val neoForgeVersions = neoForgeVersionStrings.map { LoaderVersion(version = it) }
                _neoForgeVersions.value = neoForgeVersions
                _selectedNeoForgeVersion.value = neoForgeVersions.firstOrNull()

                val quiltVersions = ApiClient.quiltApiService.getQuiltVersions(versionId)
                val mappedQuiltVersions = quiltVersions.map { it.toLoaderVersion() }
                _quiltVersions.value = mappedQuiltVersions
                _selectedQuiltVersion.value = mappedQuiltVersions.firstOrNull { it.status == "Stable" }

                val optiFineVersionTokens = ApiClient.optiFineApiService.getOptiFineVersions()
                val mappedOptiFineVersions = optiFineVersionTokens
                    .filter { it.mcVersion == versionId }
                    .map { it.toLoaderVersion() }
                _optifineVersions.value = mappedOptiFineVersions
                _selectedOptifineVersion.value = mappedOptiFineVersions.firstOrNull()

            } catch (e: Exception) { /* Handle error */ }

            // Placeholders
            _fabricApiVersions.value = listOf("0.100.0+1.21", "0.99.3+1.21", "0.96.4+1.20.6")
            _selectedFabricApiVersion.value = _fabricApiVersions.value.firstOrNull()
        }
    }

    fun setVersionName(name: String) {
        isVersionNameManuallyEdited = true
        _versionName.value = name
    }

    fun selectModLoader(loader: ModLoader) {
        _selectedModLoader.value = if (_selectedModLoader.value == loader) ModLoader.None else loader
        if (_selectedModLoader.value != ModLoader.Fabric) {
            _isFabricApiSelected.value = false
        }
        updateVersionNameSuffix()
    }

    fun toggleFabricApi(selected: Boolean) {
        _isFabricApiSelected.value = selected
    }

    fun selectFabricVersion(version: FabricLoaderVersion) {
        _selectedFabricVersion.value = version
        updateVersionNameSuffix()
    }

    fun selectFabricApiVersion(version: String) {
        _selectedFabricApiVersion.value = version
    }

    fun selectForgeVersion(version: LoaderVersion) {
        _selectedForgeVersion.value = version
        updateVersionNameSuffix()
    }

    fun selectNeoForgeVersion(version: LoaderVersion) {
        _selectedNeoForgeVersion.value = version
        updateVersionNameSuffix()
    }

    fun selectQuiltVersion(version: LoaderVersion) {
        _selectedQuiltVersion.value = version
        updateVersionNameSuffix()
    }

    fun toggleOptifine(selected: Boolean) {
        _isOptifineSelected.value = selected
        updateVersionNameSuffix()
    }

    fun selectOptifineVersion(version: LoaderVersion) {
        _selectedOptifineVersion.value = version
        updateVersionNameSuffix()
    }

    private fun updateVersionNameSuffix() {
        if (isVersionNameManuallyEdited) return

        _versionName.value = buildString {
            append(versionId)

            when (selectedModLoader.value) {
                ModLoader.Fabric -> {
                    append("-Fabric")
                    selectedFabricVersion.value?.let { append("-${it.version}") }
                }
                ModLoader.Forge -> {
                    append("-Forge")
                    selectedForgeVersion.value?.let { append("-${it.version}") }
                }
                ModLoader.NeoForge -> {
                    append("-NeoForge")
                    selectedNeoForgeVersion.value?.let { append("-${it.version}") }
                }
                ModLoader.Quilt -> {
                    append("-Quilt")
                    selectedQuiltVersion.value?.let { append("-${it.version}") }
                }
                ModLoader.None -> { /* Do nothing */ }
            }

            if (isOptifineSelected.value) {
                append("-Optifine")
                selectedOptifineVersion.value?.let { append("-${it.version}") }
            }
        }
    }

    private fun ForgeVersionToken.toLoaderVersion(): LoaderVersion {
        val isRecommended = version.endsWith("-recommended")
        val status = when {
            isRecommended -> "Recommended"
            else -> null
        }
        return LoaderVersion(
            version = version,
            releaseTime = modified,
            isRecommended = isRecommended,
            status = status
        )
    }

    private fun QuiltVersion.toLoaderVersion(): LoaderVersion {
        val status = if (stable) "Stable" else "Beta"
        return LoaderVersion(
            version = version,
            status = status
        )
    }

    private fun OptiFineVersionToken.toLoaderVersion(): LoaderVersion {
        return LoaderVersion(
            version = "${type}_${patch}"
        )
    }

    fun download() {
        viewModelScope.launch {
            try {
                val manifest = VersionManager.getVersionManifest()
                val version = manifest.versions.find { it.id == versionId }
                if (version != null) {
                    val minecraftDownloader = MinecraftDownloader(
                        getApplication(),
                        version.id,
                        customName = _versionName.value,
                        verifyIntegrity = true,
                        mode = DownloadMode.DOWNLOAD,
                        onCompletion = { _downloadTask.value = null },
                        onError = { _downloadTask.value = null }
                    )
                    _downloadTask.value = minecraftDownloader.getDownloadTask()
                } else {
                    // Handle version not found
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}