package com.lanrhyme.shardlauncher.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.HdrWeak
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.R
import com.lanrhyme.shardlauncher.ui.components.ScalingActionButton

data class OssLibrary(
    val name: String,
    val author: String,
    val url: String,
    val license: String
)

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    var showLicensesDialog by remember { mutableStateOf(false) }

    if (showLicensesDialog) {
        LicensesDialog { showLicensesDialog = false }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(0.65f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_launchertitle),
                                contentDescription = "Launcher Title Image"
                            )
                            Spacer(Modifier.width(16.dp))
                            Text("一款适用于Android和iOS的Minecraft:Java Edition启动器")
                        }
                        Spacer(Modifier.padding(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ScalingActionButton(
                                onClick = { /*TODO*/ },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Article,
                                text = "文档"
                            )
                            ScalingActionButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LanRhyme/ShardLauncher"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Code,
                                text = "Github"
                            )
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("贡献者")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_lanrhyme),
                                contentDescription = "LanRhyme avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text("LanRhyme", fontWeight = FontWeight.Bold)
                                Text(
                                    text = "项目发起者，主要开发者",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ScalingActionButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LanRhyme"))
                                        context.startActivity(intent)
                                    },
                                    icon = Icons.Default.Code,
                                    text = "Github",
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                )
                                ScalingActionButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lanrhyme.netlify.app/"))
                                        context.startActivity(intent)
                                    },
                                    icon = Icons.Filled.HdrWeak,
                                    text = "个人站点",
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("鸣谢")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("新闻主页", fontWeight = FontWeight.Bold)
                                Text(
                                    text = "启动器主页中的Minecraft更新卡片",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ScalingActionButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Light-Beacon/PCL2-NewsHomepage"))
                                        context.startActivity(intent)
                                    },
                                    icon = Icons.Default.Code,
                                    text = "Github",
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                )
                                ScalingActionButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://news.bugjump.net/static/"))
                                        context.startActivity(intent)
                                    },
                                    icon = Icons.Filled.Link,
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Zalith Launcher2", fontWeight = FontWeight.Bold)
                                Text(
                                    text = "修改使用部分控件代码，参考页面布局",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ScalingActionButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ZalithLauncher/ZalithLauncher2"))
                                        context.startActivity(intent)
                                    },
                                    icon = Icons.Default.Code,
                                    text = "Github",
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("开源许可")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "本应用使用了许多优秀的开源库来构建",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ScalingActionButton(
                                    onClick = { showLicensesDialog = true },
                                    icon = Icons.Default.Article,
                                    text = "查看许可",
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.weight(0.35f)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("版本信息")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("版本: ")
                            }
                            append(stringResource(id = R.string.version_name))
                        })
                        Spacer(Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = stringResource(id = R.string.git_hash),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("分支: ")
                        }
                        append(stringResource(id = R.string.git_branch))
                    })
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("版本状态: ")
                        }
                        append(stringResource(id = R.string.build_status))
                    })
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("上次更新时间: ")
                        }
                        append(stringResource(id = R.string.last_update_time))
                    })
                    Spacer(Modifier.height(8.dp))
                    ScalingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Filled.SystemUpdate,
                        text = "检查更新"
                    )
                }
            }
        }
    }
}

@Composable
fun LicensesDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val libraries = remember {
        listOf(
            OssLibrary("Jetpack Compose", "Google", "https://developer.android.com/jetpack/compose", "Apache License 2.0"),
            OssLibrary("AndroidX", "Google", "https://source.android.com/", "Apache License 2.0"),
            OssLibrary("Coil", "Coil Contributors", "https://coil-kt.github.io/coil/", "Apache License 2.0"),
            OssLibrary("Retrofit", "Square, Inc.", "https://square.github.io/retrofit/", "Apache License 2.0"),
            OssLibrary("Gson", "Google", "https://github.com/google/gson", "Apache License 2.0"),
            OssLibrary("PCL2-NewsHomepage", "Light-Beacon", "https://github.com/Light-Beacon/PCL2-NewsHomepage", "MIT License"),
            OssLibrary("ZalithLauncher2", "ZalithLauncher", "https://github.com/ZalithLauncher/ZalithLauncher2", "GNU GPL v3.0"),
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Open Source Libraries and Licenses") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(libraries) { lib ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lib.url))
                                context.startActivity(intent)
                            }
                    ) {
                        Text(lib.name, fontWeight = FontWeight.Bold)
                        Text("by ${lib.author}", style = MaterialTheme.typography.bodySmall)
                        Text(lib.license, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
