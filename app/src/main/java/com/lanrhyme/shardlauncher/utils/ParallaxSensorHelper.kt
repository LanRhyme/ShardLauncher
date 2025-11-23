package com.lanrhyme.shardlauncher.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

data class ParallaxValues(val x: Float, val y: Float)

@Composable
fun rememberParallaxSensorHelper(
    enableParallax: Boolean,
    parallaxMagnitude: Float
): State<ParallaxValues> {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val gravitySensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) }
    val parallaxState = remember { mutableStateOf(ParallaxValues(0f, 0f)) }
    val windowManager = remember(context) { context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager }

    if (!enableParallax || gravitySensor == null || windowManager == null) {
        //如果视差被禁用或传感器不存在，确保状态重置且不做任何作
        if (parallaxState.value.x != 0f || parallaxState.value.y != 0f) {
            parallaxState.value = ParallaxValues(0f, 0f)
        }
        return parallaxState
    }

    val listener = remember(parallaxMagnitude, windowManager) {
        object : SensorEventListener {
            private var lastX = 0f
            private var lastY = 0f
            private val alpha = 0.2f // 低通滤波器的平滑因子, 增加以提高响应速度

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    lastX = lastX + alpha * (it.values[0] - lastX)
                    lastY = lastY + alpha * (it.values[1] - lastY)

                    // 根据目标幅度调整倍数
                    val multiplier = parallaxMagnitude * 3f

                    // 根据屏幕方向调整视差
                    @Suppress("DEPRECATION")
                    val rotation = windowManager.defaultDisplay.rotation

                    val (x, y) = when (rotation) {
                        Surface.ROTATION_90 -> ParallaxValues(x = lastY * multiplier, y = lastX * multiplier)
                        Surface.ROTATION_180 -> ParallaxValues(x = lastX * multiplier, y = -lastY * multiplier)
                        Surface.ROTATION_270 -> ParallaxValues(x = -lastY * multiplier, y = -lastX * multiplier)
                        else -> ParallaxValues(x = -lastX * multiplier, y = lastY * multiplier) // ROTATION_0 for portrait
                    }
                    parallaxState.value = ParallaxValues(x, y)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(gravitySensor, parallaxMagnitude) {
        // 使用 SENSOR_DELAY_GAME 以获得更平滑的动画效果
        sensorManager.registerListener(listener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return parallaxState
}
