package com.lanrhyme.shardlauncher.ui.developeroptions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.ui.components.SliderLayout
import com.lanrhyme.shardlauncher.ui.components.TitleAndSummary

@Composable
fun DeveloperOptionsScreen(
    animationSpeed: Float,
    onAnimationSpeedChange: (Float) -> Unit
) {
    val animatedSpeed by animateFloatAsState(targetValue = animationSpeed)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleAndSummary(
                title = "开发者选项",
                summary = "调整应用内动画速率"
            )

            Spacer(modifier = Modifier.height(32.dp))

            SliderLayout(
                value = animationSpeed,
                onValueChange = { onAnimationSpeedChange(it) },
                valueRange = 0.5f..2f,
                steps = 14,
                title = "动画速率",
                summary = "控制 UI 动画的播放速度",
                displayValue = animatedSpeed
            )
        }
    }
}
