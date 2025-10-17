package com.lanrhyme.shardlauncher.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lanrhyme.shardlauncher.api.ApiClient
import com.lanrhyme.shardlauncher.model.FabricLoaderVersion
import com.lanrhyme.shardlauncher.model.LoaderVersion
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

class VersionDetailViewModel(private val versionId: String) : ViewModel() {

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

    init {
        loadAllLoaderVersions()
    }

    private fun loadAllLoaderVersions() {
        viewModelScope.launch {
            try {
                val versions = ApiClient.fabricApiService.getLoaderVersions()
                _fabricVersions.value = versions
                _selectedFabricVersion.value = versions.firstOrNull { it.stable }
            } catch (e: Exception) { /* Handle error */ }

            // Placeholders
            _fabricApiVersions.value = listOf("0.100.0+1.21", "0.99.3+1.21", "0.96.4+1.20.6")
            _selectedFabricApiVersion.value = _fabricApiVersions.value.firstOrNull()
            _forgeVersions.value = listOf(
                LoaderVersion("47.0.35", status = "Recommended", releaseTime = "2023-11-14", isRecommended = true),
                LoaderVersion("47.0.30", status = "Latest", releaseTime = "2023-11-12"),
                LoaderVersion("46.0.14", releaseTime = "2023-08-01")
            )
            _selectedForgeVersion.value = _forgeVersions.value.firstOrNull()

            _neoForgeVersions.value = listOf(
                LoaderVersion("20.4.19-beta", status = "Beta", releaseTime = "2024-03-15"),
                LoaderVersion("20.4.12", releaseTime = "2024-03-01")
            )
            _selectedNeoForgeVersion.value = _neoForgeVersions.value.firstOrNull()

            _quiltVersions.value = listOf(
                LoaderVersion("0.20.0", releaseTime = "2024-02-28"),
                LoaderVersion("0.19.4", releaseTime = "2024-01-15")
            )
            _selectedQuiltVersion.value = _quiltVersions.value.firstOrNull()

            _optifineVersions.value = listOf(
                LoaderVersion("U-I6", releaseTime = "2023-12-25"),
                LoaderVersion("U-I5", releaseTime = "2023-12-01")
            )
            _selectedOptifineVersion.value = _optifineVersions.value.firstOrNull()
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

    fun download() { /* TODO */ }
}
