package com.lanrhyme.shardlauncher.ui.downloads

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lanrhyme.shardlauncher.model.FabricLoaderVersion
import com.lanrhyme.shardlauncher.model.LoaderVersion
import com.lanrhyme.shardlauncher.ui.components.CombinedCard
import com.lanrhyme.shardlauncher.ui.components.CustomTextField
import com.lanrhyme.shardlauncher.ui.components.ScalingActionButton
import com.lanrhyme.shardlauncher.ui.components.StyledFilterChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VersionDetailScreen(navController: NavController, versionId: String?) {
    if (versionId == null) {
        Text("Error: Version ID is missing.")
        return
    }

    val viewModel: VersionDetailViewModel = viewModel { VersionDetailViewModel(versionId) }
    val versionName by viewModel.versionName.collectAsState()
    val selectedModLoader by viewModel.selectedModLoader.collectAsState()
    val isOptifineSelected by viewModel.isOptifineSelected.collectAsState()
    val isFabricApiSelected by viewModel.isFabricApiSelected.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "安装 $versionId",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                value = versionName,
                onValueChange = { viewModel.setVersionName(it) },
                label = "版本名称",
                modifier = Modifier.fillMaxWidth()
            )

            CombinedCard(title = "模组加载器", summary = "选择一个模组加载器 (可选)") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StyledFilterChip(selected = selectedModLoader == ModLoader.Fabric, onClick = { viewModel.selectModLoader(ModLoader.Fabric) }, label = { Text("Fabric") })
                        StyledFilterChip(selected = selectedModLoader == ModLoader.Forge, onClick = { viewModel.selectModLoader(ModLoader.Forge) }, label = { Text("Forge") })
                        StyledFilterChip(selected = selectedModLoader == ModLoader.NeoForge, onClick = { viewModel.selectModLoader(ModLoader.NeoForge) }, label = { Text("NeoForge") })
                        StyledFilterChip(selected = selectedModLoader == ModLoader.Quilt, onClick = { viewModel.selectModLoader(ModLoader.Quilt) }, label = { Text("Quilt") })
                    }

                    AnimatedVisibility(visible = selectedModLoader == ModLoader.Fabric) {
                        Column {
                            val fabricVersions by viewModel.fabricVersions.collectAsState()
                            val selectedVersion by viewModel.selectedFabricVersion.collectAsState()
                            LoaderVersionDropdown(versions = fabricVersions, selectedVersion = selectedVersion, onVersionSelected = { viewModel.selectFabricVersion(it as FabricLoaderVersion) })
                            
                            StyledFilterChip(
                                selected = isFabricApiSelected,
                                onClick = { viewModel.toggleFabricApi(!isFabricApiSelected) },
                                label = { Text("同时安装 Fabric API") }
                            )

                            AnimatedVisibility(visible = isFabricApiSelected) {
                                val fabricApiVersions by viewModel.fabricApiVersions.collectAsState()
                                val selectedApiVersion by viewModel.selectedFabricApiVersion.collectAsState()
                                LoaderVersionDropdown(versions = fabricApiVersions, selectedVersion = selectedApiVersion, onVersionSelected = { viewModel.selectFabricApiVersion(it as String) })
                            }
                        }
                    }

                    AnimatedVisibility(visible = selectedModLoader == ModLoader.Forge) {
                         val forgeVersions by viewModel.forgeVersions.collectAsState()
                         val selectedVersion by viewModel.selectedForgeVersion.collectAsState()
                         LoaderVersionDropdown(versions = forgeVersions, selectedVersion = selectedVersion, onVersionSelected = { viewModel.selectForgeVersion(it as LoaderVersion) })
                    }
                     AnimatedVisibility(visible = selectedModLoader == ModLoader.NeoForge) {
                         val neoForgeVersions by viewModel.neoForgeVersions.collectAsState()
                         val selectedVersion by viewModel.selectedNeoForgeVersion.collectAsState()
                         LoaderVersionDropdown(versions = neoForgeVersions, selectedVersion = selectedVersion, onVersionSelected = { viewModel.selectNeoForgeVersion(it as LoaderVersion) })
                    }
                     AnimatedVisibility(visible = selectedModLoader == ModLoader.Quilt) {
                         val quiltVersions by viewModel.quiltVersions.collectAsState()
                         val selectedVersion by viewModel.selectedQuiltVersion.collectAsState()
                         LoaderVersionDropdown(versions = quiltVersions, selectedVersion = selectedVersion, onVersionSelected = { viewModel.selectQuiltVersion(it as LoaderVersion) })
                    }
                }
            }

            CombinedCard(title = "光影加载器", summary = "为你的游戏添加光影 (可选)") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StyledFilterChip(
                        selected = isOptifineSelected,
                        onClick = { viewModel.toggleOptifine(!isOptifineSelected) },
                        label = { Text("Optifine") }
                    )

                    AnimatedVisibility(visible = isOptifineSelected) {
                        val optifineVersions by viewModel.optifineVersions.collectAsState()
                        val selectedVersion by viewModel.selectedOptifineVersion.collectAsState()
                        LoaderVersionDropdown(
                            versions = optifineVersions,
                            selectedVersion = selectedVersion,
                            onVersionSelected = { viewModel.selectOptifineVersion(it as LoaderVersion) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ScalingActionButton(
            onClick = { viewModel.download() },
            modifier = Modifier.fillMaxWidth(),
            text = "下载"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> LoaderVersionDropdown(
    versions: List<T>,
    selectedVersion: T?,
    onVersionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedVersionText = when (selectedVersion) {
        is FabricLoaderVersion -> selectedVersion.version
        is LoaderVersion -> selectedVersion.version
        is String -> selectedVersion
        else -> ""
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        CustomTextField(
            value = selectedVersionText,
            onValueChange = {},
            readOnly = true,
            label = "版本",
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            versions.forEach { version ->
                DropdownMenuItem(
                    text = { VersionDropdownItem(version = version) },
                    onClick = {
                        onVersionSelected(version)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun <T> VersionDropdownItem(version: T) {
    when (version) {
        is FabricLoaderVersion -> {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(version.version, style = MaterialTheme.typography.bodyMedium)
                val status = if (version.stable == true) "Stable" else "Beta"
                val color = if (version.stable == true) Color(0xFF4CAF50) else Color(0xFFFFA000)
                Text(
                    text = status,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        is LoaderVersion -> {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(version.version, style = MaterialTheme.typography.bodyMedium)
                        version.status?.let {
                            val color = if (version.isRecommended) Color(0xFF4CAF50) else Color(0xFFFFA000)
                            Text(
                                text = it,
                                color = Color.White,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    version.releaseTime?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        is String -> {
            Text(version, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
