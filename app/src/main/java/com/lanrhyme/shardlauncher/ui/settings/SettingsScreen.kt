package com.lanrhyme.shardlauncher.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lanrhyme.shardlauncher.common.SidebarPosition
import com.lanrhyme.shardlauncher.ui.components.CustomCard
import com.lanrhyme.shardlauncher.ui.theme.ShardLauncherTheme

// 1. 定义设置页面分类
enum class SettingsPage(val title: String) {
    Launcher("启动器设置"),
    Game("全局游戏设置"),
    Controls("控制设置"),
    About("关于"),
    Other("其他")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    sidebarPosition: SidebarPosition,
    onPositionChange: (SidebarPosition) -> Unit
) {
    ShardLauncherTheme(darkTheme = isDarkTheme) {
        // 2. 添加状态来管理当前选中的页面
        var selectedPage by remember { mutableStateOf(SettingsPage.Launcher) }
        val pages = SettingsPage.entries

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "设置",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 16.dp)
                )
                PrimaryTabRow(
                    selectedTabIndex = selectedPage.ordinal,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp)),
                ) {
                    pages.forEachIndexed { index, page ->
                        Tab(
                            selected = selectedPage.ordinal == index,
                            onClick = { selectedPage = pages[index] },
                            text = { Text(text = page.title) }
                        )
                    }
                }
            }

            // 4. 根据选中的页面显示不同的内容
            // 目前，仅当选中“启动器设置”时显示现有内容
            when (selectedPage) {
                SettingsPage.Launcher -> {
                    LauncherSettingsContent(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        sidebarPosition = sidebarPosition,
                        onPositionChange = onPositionChange
                    )
                }
                // 其他分类页面的内容可以稍后添加
                SettingsPage.Game -> { /* TODO: 全局游戏设置内容 */ }
                SettingsPage.Controls -> { /* TODO: 控制设置内容 */ }
                SettingsPage.About -> { /* TODO: 关于页面内容 */ }
                SettingsPage.Other -> { /* TODO: 其他设置内容 */ }
            }
        }
    }
}

// 将原有的设置内容提取到一个独立的Composable函数中
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LauncherSettingsContent(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    sidebarPosition: SidebarPosition,
    onPositionChange: (SidebarPosition) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CustomCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "显示设置",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))

                    // Dark Mode Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "深色模式",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onThemeToggle() }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Sidebar Position Setting
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "侧边栏位置",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = if (sidebarPosition == SidebarPosition.Left) "左侧" else "右侧",
                                onValueChange = {},
                                readOnly = true,
                                shape = RoundedCornerShape(22.dp),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            )

                            AnimatedVisibility(
                                visible = expanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("左侧") },
                                        onClick = {
                                            onPositionChange(SidebarPosition.Left)
                                            expanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("右侧") },
                                        onClick = {
                                            onPositionChange(SidebarPosition.Right)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
