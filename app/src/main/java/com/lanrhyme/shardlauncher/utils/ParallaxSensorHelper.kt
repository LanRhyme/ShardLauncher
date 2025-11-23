package com.lanrhyme.shardlauncher.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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

    if (!enableParallax || gravitySensor == null) {
        //如果视差被禁用或传感器不存在，确保状态重置且不做任何作
        if (parallaxState.value.x != 0f || parallaxState.value.y != 0f) {
            parallaxState.value = ParallaxValues(0f, 0f)
        }
        return parallaxState
    }

    val listener = remember(parallaxMagnitude) {
        object : SensorEventListener {
            private var lastX = 0f
            private var lastY = 0f
            private val alpha = 0.1f // 低通滤波器的平滑因子

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    lastX = lastX + alpha * (it.values[0] - lastX)
                    lastY = lastY + alpha * (it.values[1] - lastY)

                    // 根据目标幅度调整倍数
                    val multiplier = parallaxMagnitude * 3f

                    // 将背景移动到与设备倾斜方向相反的方向
                    // 向右倾斜（正 X 重力）会使背景向左移动（负平移X）
                    // 将顶部倾斜向用户（正Y重力）会使背景向下移动（正平移Y。
                    parallaxState.value = ParallaxValues(
                        x = -lastX * multiplier,
                        y = lastY * multiplier
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(gravitySensor, parallaxMagnitude) {
        sensorManager.registerListener(listener, gravitySensor, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return parallaxState
}
