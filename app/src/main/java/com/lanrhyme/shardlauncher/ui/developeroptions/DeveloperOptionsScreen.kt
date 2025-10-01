package com.lanrhyme.shardlauncher.ui.developeroptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.ui.components.ScalingActionButton
import com.lanrhyme.shardlauncher.ui.components.TitleAndSummary

@Composable
fun DeveloperOptionsScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TitleAndSummary(
                title = "欢迎来到开发者选项",
                summary = "这里展示了自定义UI组件",
                modifier = Modifier.padding(bottom = 32.dp)
            )

            ScalingActionButton(
                onClick = { /* Handle click */ },
                icon = Icons.Filled.PlayArrow,
                text = "开始游戏",
                modifier = Modifier.height(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ScalingActionButton(
                onClick = { /* Handle click */ },
                text = "设置选项",
                modifier = Modifier.height(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "点击按钮体验缩放效果",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    }
}