package com.lanrhyme.shardlauncher.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    var animationState by remember { mutableStateOf(0) } // 0: initial, 1: enter, 2: exit

    val yOffset by animateDpAsState(
        targetValue = if (animationState >= 1) 0.dp else 150.dp,
        animationSpec = tween(durationMillis = 500),
        label = "yOffsetAnim"
    )

    val rotationZ by animateFloatAsState(
        targetValue = if (animationState >= 1) 0f else -15f,
        animationSpec = tween(durationMillis = 500),
        label = "rotationZAnim"
    )

    val alpha by animateFloatAsState(
        targetValue = if (animationState == 1) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "alphaAnim"
    )

    val scale by animateFloatAsState(
        targetValue = if (animationState == 2) 1.5f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "scaleAnim"
    )

    val blur by animateDpAsState(
        targetValue = if (animationState == 2) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 500),
        label = "blurAnim"
    )

    LaunchedEffect(Unit) {
        animationState = 1 // Move to center and fade in
        delay(500)
        animationState = 2 // Scale, blur, and fade out
        delay(500)
        onAnimationFinished()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val gradientBrush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.tertiary
                )
            )
            Text(
                text = "Welcome to ShardLauncher!",
                style = TextStyle(
                    brush = gradientBrush,
                    fontSize = 24.sp
                ),
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                        this.rotationZ = rotationZ
                        this.translationY = yOffset.toPx()
                    }
                    .blur(blur),
            )
        }
    }
}