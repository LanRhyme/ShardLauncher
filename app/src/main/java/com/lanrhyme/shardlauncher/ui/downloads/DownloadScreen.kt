package com.lanrhyme.shardlauncher.ui.downloads

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.lanrhyme.shardlauncher.ui.components.SegmentedNavigationBar

// 1. 定义下载页面分类
enum class DownloadPage(val title: String) {
    Game("游戏"),
    Modpack("整合包"),
    Mod("模组"),
    ResourcePack("资源包"),
    Map("地图"),
    Shader("光影")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    navController: NavController
) {
    // 2. 添加状态来管理当前选中的页面
    var selectedPage by remember { mutableStateOf(DownloadPage.Game) }
    val pages = DownloadPage.entries

    Column(modifier = Modifier.fillMaxSize()) {
        SegmentedNavigationBar(
            title = "下载",
            selectedPage = selectedPage,
            onPageSelected = { selectedPage = it },
            pages = pages.toList(),
            getTitle = { it.title }
        )

        // 4. 根据选中的页面显示不同的内容
        AnimatedContent(targetState = selectedPage, label = "Download Page Animation", transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }) { page ->
            when (page) {
                DownloadPage.Game -> {
                    GameDownloadContent()
                }
                DownloadPage.Modpack -> {
                    // Placeholder for Modpack content
                }
                DownloadPage.Mod -> {
                    // Placeholder for Mod content
                }
                DownloadPage.ResourcePack -> {
                    // Placeholder for ResourcePack content
                }
                DownloadPage.Map -> {
                    // Placeholder for Map content
                }
                DownloadPage.Shader -> {
                    // Placeholder for Shader content
                }
            }
        }
    }
}
