package com.lanrhyme.shardlauncher.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private data class ColorProperty(val name: String, val title: String, val summary: String)

private val colorSchemeProperties = listOf(
    ColorProperty("primary", "primary", "应用中最突出的颜色"),
    ColorProperty("onPrimary", "onPrimary", "主色背景之上的文本和图标"),
    ColorProperty("secondary", "secondary", "用于界面元素的点缀"),
    ColorProperty("onSecondary", "onSecondary", "次要色背景之上的文本和图标"),
    ColorProperty("tertiary", "tertiary", "用于平衡主次颜色或突出元素"),
    ColorProperty("onTertiary", "onTertiary", "第三色背景之上的文本和图标"),
    ColorProperty("background", "background", "可滚动内容区域的背景"),
    ColorProperty("onBackground", "onBackground", "背景颜色之上的文本和图标"),
    ColorProperty("surface", "surface", "组件表面的颜色，如卡片、菜单"),
    ColorProperty("onSurface", "onSurface", "表面颜色之上的文本和图标"),
    ColorProperty("surfaceVariant", "surfaceVariant", "用于区分UI元素的表面变体"),
    ColorProperty("onSurfaceVariant", "onSurfaceVariant", "表面变体颜色之上的文本和图标"),
    ColorProperty("error", "error", "用于指示错误的颜色"),
    ColorProperty("onError", "onError", "错误颜色背景之上的文本和图标"),
    ColorProperty("errorContainer", "errorContainer", "用于承载错误信息的容器颜色"),
    ColorProperty("onErrorContainer", "onErrorContainer", "错误容器颜色之上的文本和图标")
)

private fun getColorByName(scheme: ColorScheme, name: String): Color {
    return when (name) {
        "primary" -> scheme.primary
        "onPrimary" -> scheme.onPrimary
        "secondary" -> scheme.secondary
        "onSecondary" -> scheme.onSecondary
        "tertiary" -> scheme.tertiary
        "onTertiary" -> scheme.onTertiary
        "background" -> scheme.background
        "onBackground" -> scheme.onBackground
        "surface" -> scheme.surface
        "onSurface" -> scheme.onSurface
        "surfaceVariant" -> scheme.surfaceVariant
        "onSurfaceVariant" -> scheme.onSurfaceVariant
        "error" -> scheme.error
        "onError" -> scheme.onError
        "errorContainer" -> scheme.errorContainer
        "onErrorContainer" -> scheme.onErrorContainer
        else -> Color.Unspecified
    }
}

private fun setColorByName(scheme: ColorScheme, name: String, color: Color): ColorScheme {
    return when (name) {
        "primary" -> scheme.copy(primary = color)
        "onPrimary" -> scheme.copy(onPrimary = color)
        "secondary" -> scheme.copy(secondary = color)
        "onSecondary" -> scheme.copy(onSecondary = color)
        "tertiary" -> scheme.copy(tertiary = color)
        "onTertiary" -> scheme.copy(onTertiary = color)
        "background" -> scheme.copy(background = color)
        "onBackground" -> scheme.copy(onBackground = color)
        "surface" -> scheme.copy(surface = color)
        "onSurface" -> scheme.copy(onSurface = color)
        "surfaceVariant" -> scheme.copy(surfaceVariant = color)
        "onSurfaceVariant" -> scheme.copy(onSurfaceVariant = color)
        "error" -> scheme.copy(error = color)
        "onError" -> scheme.copy(onError = color)
        "errorContainer" -> scheme.copy(errorContainer = color)
        "onErrorContainer" -> scheme.copy(onErrorContainer = color)
        else -> scheme
    }
}


@Composable
fun ThemeColorEditor(
    lightColorScheme: ColorScheme,
    darkColorScheme: ColorScheme,
    onLightColorSchemeChange: (ColorScheme) -> Unit,
    onDarkColorSchemeChange: (ColorScheme) -> Unit
) {
    if (colorSchemeProperties.isEmpty()) {
        // This should not happen with the new static list
        Text("Error: No color properties found.")
        return
    }

    var selectedPropertyName by remember { mutableStateOf(colorSchemeProperties.first().name) }
    var isDarkTheme by remember { mutableStateOf(false) }

    val currentColorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme
    val onColorSchemeChange = if (isDarkTheme) onDarkColorSchemeChange else onLightColorSchemeChange

    val selectedColor = getColorByName(currentColorScheme, selectedPropertyName)

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            TabRow(
                selectedTabIndex = if (isDarkTheme) 1 else 0,
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            ) {
                Tab(
                    selected = !isDarkTheme,
                    onClick = { isDarkTheme = false },
                    text = { Text("Light") }
                )
                Tab(
                    selected = isDarkTheme,
                    onClick = { isDarkTheme = true },
                    text = { Text("Dark") }
                )
            }
            LazyColumn {
                items(colorSchemeProperties) { prop ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPropertyName = prop.name }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prop.title, style = MaterialTheme.typography.titleMedium)
                            Text(prop.summary, style = MaterialTheme.typography.bodySmall)
                        }
                        Canvas(modifier = Modifier
                            .width(32.dp)
                            .height(16.dp)) {
                            drawRect(color = getColorByName(currentColorScheme, prop.name))
                        }
                    }
                }
            }
        }

        Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
            HsvColorPicker(
                color = selectedColor,
                onColorSelected = { newColor ->
                    val newColorScheme = setColorByName(currentColorScheme, selectedPropertyName, newColor)
                    onColorSchemeChange(newColorScheme)
                }
            )
        }
    }
}