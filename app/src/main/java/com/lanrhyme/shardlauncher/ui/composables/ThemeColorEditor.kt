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
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val colorSchemeProperties = listOf(
    "primary", "onPrimary", "primaryContainer", "onPrimaryContainer",
    "inversePrimary", "secondary", "onSecondary", "secondaryContainer",
    "onSecondaryContainer", "tertiary", "onTertiary", "tertiaryContainer",
    "onTertiaryContainer", "background", "onBackground", "surface", "onSurface",
    "surfaceVariant", "onSurfaceVariant", "surfaceTint", "inverseSurface",
    "inverseOnSurface", "error", "onError", "errorContainer", "onErrorContainer",
    "outline", "outlineVariant", "scrim"
)

private fun getColorByName(scheme: ColorScheme, name: String): Color {
    return when (name) {
        "primary" -> scheme.primary
        "onPrimary" -> scheme.onPrimary
        "primaryContainer" -> scheme.primaryContainer
        "onPrimaryContainer" -> scheme.onPrimaryContainer
        "inversePrimary" -> scheme.inversePrimary
        "secondary" -> scheme.secondary
        "onSecondary" -> scheme.onSecondary
        "secondaryContainer" -> scheme.secondaryContainer
        "onSecondaryContainer" -> scheme.onSecondaryContainer
        "tertiary" -> scheme.tertiary
        "onTertiary" -> scheme.onTertiary
        "tertiaryContainer" -> scheme.tertiaryContainer
        "onTertiaryContainer" -> scheme.onTertiaryContainer
        "background" -> scheme.background
        "onBackground" -> scheme.onBackground
        "surface" -> scheme.surface
        "onSurface" -> scheme.onSurface
        "surfaceVariant" -> scheme.surfaceVariant
        "onSurfaceVariant" -> scheme.onSurfaceVariant
        "surfaceTint" -> scheme.surfaceTint
        "inverseSurface" -> scheme.inverseSurface
        "inverseOnSurface" -> scheme.inverseOnSurface
        "error" -> scheme.error
        "onError" -> scheme.onError
        "errorContainer" -> scheme.errorContainer
        "onErrorContainer" -> scheme.onErrorContainer
        "outline" -> scheme.outline
        "outlineVariant" -> scheme.outlineVariant
        "scrim" -> scheme.scrim
        else -> Color.Unspecified
    }
}

private fun setColorByName(scheme: ColorScheme, name: String, color: Color): ColorScheme {
    return when (name) {
        "primary" -> scheme.copy(primary = color)
        "onPrimary" -> scheme.copy(onPrimary = color)
        "primaryContainer" -> scheme.copy(primaryContainer = color)
        "onPrimaryContainer" -> scheme.copy(onPrimaryContainer = color)
        "inversePrimary" -> scheme.copy(inversePrimary = color)
        "secondary" -> scheme.copy(secondary = color)
        "onSecondary" -> scheme.copy(onSecondary = color)
        "secondaryContainer" -> scheme.copy(secondaryContainer = color)
        "onSecondaryContainer" -> scheme.copy(onSecondaryContainer = color)
        "tertiary" -> scheme.copy(tertiary = color)
        "onTertiary" -> scheme.copy(onTertiary = color)
        "tertiaryContainer" -> scheme.copy(tertiaryContainer = color)
        "onTertiaryContainer" -> scheme.copy(onTertiaryContainer = color)
        "background" -> scheme.copy(background = color)
        "onBackground" -> scheme.copy(onBackground = color)
        "surface" -> scheme.copy(surface = color)
        "onSurface" -> scheme.copy(onSurface = color)
        "surfaceVariant" -> scheme.copy(surfaceVariant = color)
        "onSurfaceVariant" -> scheme.copy(onSurfaceVariant = color)
        "surfaceTint" -> scheme.copy(surfaceTint = color)
        "inverseSurface" -> scheme.copy(inverseSurface = color)
        "inverseOnSurface" -> scheme.copy(inverseOnSurface = color)
        "error" -> scheme.copy(error = color)
        "onError" -> scheme.copy(onError = color)
        "errorContainer" -> scheme.copy(errorContainer = color)
        "onErrorContainer" -> scheme.copy(onErrorContainer = color)
        "outline" -> scheme.copy(outline = color)
        "outlineVariant" -> scheme.copy(outlineVariant = color)
        "scrim" -> scheme.copy(scrim = color)
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

    var selectedPropertyName by remember { mutableStateOf(colorSchemeProperties.first()) }
    var isDarkTheme by remember { mutableStateOf(false) }

    val currentColorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme
    val onColorSchemeChange = if (isDarkTheme) onDarkColorSchemeChange else onLightColorSchemeChange

    val selectedColor = getColorByName(currentColorScheme, selectedPropertyName)

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            TabRow(selectedTabIndex = if (isDarkTheme) 1 else 0) {
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
                items(colorSchemeProperties) { propName ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPropertyName = propName }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(propName)
                        Canvas(modifier = Modifier.width(32.dp).height(16.dp)) {
                            drawRect(color = getColorByName(currentColorScheme, propName))
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
