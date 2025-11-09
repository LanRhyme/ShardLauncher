package com.lanrhyme.shardlauncher.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeColor(val title: String) {
    Green("草碎影"),
    Blue("蓝璃梦"),
    Purple("紫晶泪"),
    Golden("黄粱残"),
    Dynamic("动态（Android 12+）"),
    Custom("自定义"),
}

data class ColorSet(
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
)

// Helper function to linearly interpolate between two colors
private fun lerp(start: Color, stop: Color, fraction: Float): Color {
    val r = start.red + fraction * (stop.red - start.red)
    val g = start.green + fraction * (stop.green - start.green)
    val b = start.blue + fraction * (stop.blue - start.blue)
    val a = start.alpha + fraction * (stop.alpha - start.alpha)
    return Color(red = r, green = g, blue = b, alpha = a)
}

/**
 * Generates a light and dark color scheme from a given primary color.
 * This is a simplified approach. For more advanced theming, you might use a
 * library that can generate full tonal palettes.
 */
fun generateColorSetFromPrimary(primaryColor: Color): ColorSet {
    // Light Theme Colors
    val lightSecondary = lerp(primaryColor, Color(0xFF888888), 0.2f) // Desaturate for secondary
    val lightTertiary = lerp(primaryColor, Color(0xFF888888), 0.4f)  // Desaturate more for tertiary

    // Dark Theme Colors (generally lighter and less saturated than their light theme counterparts)
    val darkPrimary = lerp(primaryColor, Color.White, 0.3f)
    val darkSecondary = lerp(darkPrimary, Color.White, 0.2f)
    val darkTertiary = lerp(darkPrimary, Color.White, 0.4f)


    val lightScheme = lightColorScheme(
        primary = primaryColor,
        secondary = lightSecondary,
        tertiary = lightTertiary,
        background = LightBackgroundColor,
        surface = LightSurfaceColor,
        error = Error80,
        errorContainer = ErrorSurface80,
        onErrorContainer = Error80,
        surfaceVariant = lightsurface
    )

    val darkScheme = darkColorScheme(
        primary = darkPrimary,
        secondary = darkSecondary,
        tertiary = darkTertiary,
        background = DarkBackgroundColor,
        surface = DarkSurfaceColor,
        error = Error40,
        errorContainer = ErrorSurface40,
        onErrorContainer = Error40,
        surfaceVariant = darksurface
    )

    return ColorSet(lightScheme, darkScheme)
}


object ColorPalettes {
    val Green = ColorSet(
        lightColorScheme = lightColorScheme(
            primary = lightGreen,
            secondary = lightGreenGrey,
            tertiary = lightGreenTertiary,
            background = LightBackgroundColor,
            surface = LightSurfaceColor,
            error = Error80,
            errorContainer = ErrorSurface80,
            onErrorContainer = Error80,
            surfaceVariant = lightsurface
        ),
        darkColorScheme = darkColorScheme(
            primary = darkGreen,
            secondary = darkGreenGrey,
            tertiary = darkGreenTertiary,
            background = DarkBackgroundColor,
            surface = DarkSurfaceColor,
            error = Error40,
            errorContainer = ErrorSurface40,
            onErrorContainer = Error40,
            surfaceVariant = darksurface
        )
    )

    val Blue = ColorSet(
        lightColorScheme = lightColorScheme(
            primary = lightBlue,
            secondary = lightBlueGrey,
            tertiary = lightBlueTertiary,
            background = LightBackgroundColor,
            surface = LightSurfaceColor,
            error = Error80,
            errorContainer = ErrorSurface80,
            onErrorContainer = Error80,
            surfaceVariant = lightsurface
        ),
        darkColorScheme = darkColorScheme(
            primary = darkBlue,
            secondary = darkBlueGrey,
            tertiary = darkBlueTertiary,
            background = DarkBackgroundColor,
            surface = DarkSurfaceColor,
            error = Error40,
            errorContainer = ErrorSurface40,
            onErrorContainer = Error40,
            surfaceVariant = darksurface
        )
    )

    val Purple = ColorSet(
        lightColorScheme = lightColorScheme(
            primary = lightPurple,
            secondary = lightPurpleGrey,
            tertiary = lightPink,
            background = LightBackgroundColor,
            surface = LightSurfaceColor,
            error = Error80,
            errorContainer = ErrorSurface80,
            onErrorContainer = Error80,
            surfaceVariant = lightsurface
        ),
        darkColorScheme = darkColorScheme(
            primary = darkPurple,
            secondary = darkPurpleGrey,
            tertiary = darkPink,
            background = DarkBackgroundColor,
            surface = DarkSurfaceColor,
            error = Error40,
            errorContainer = ErrorSurface40,
            onErrorContainer = Error40,
            surfaceVariant = darksurface
        )
    )

    val Golden = ColorSet(
        lightColorScheme = lightColorScheme(
            primary = lightYellow,
            secondary = lightYellowGrey,
            tertiary = lightYellowTertiary,
            background = LightBackgroundColor,
            surface = LightSurfaceColor,
            error = Error80,
            errorContainer = ErrorSurface80,
            onErrorContainer = Error80,
            surfaceVariant = lightsurface
        ),
        darkColorScheme = darkColorScheme(
            primary = darkYellow,
            secondary = darkYellowGrey,
            tertiary = darkYellowTertiary,
            background = DarkBackgroundColor,
            surface = DarkSurfaceColor,
            error = Error40,
            errorContainer = ErrorSurface40,
            onErrorContainer = Error40,
            surfaceVariant = darksurface
        )
    )
}

@Composable
fun ShardLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: ThemeColor = ThemeColor.Green, // Default to Green
    customPrimaryColor: Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        themeColor == ThemeColor.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context).copy(
                    surfaceVariant = darksurface,
                    background = DarkBackgroundColor
                )
            } else {
                dynamicLightColorScheme(context).copy(
                    surface = LightSurfaceColor,
                    surfaceVariant = lightsurface,
                    background = LightBackgroundColor
                )
            }
        }
        themeColor == ThemeColor.Custom && customPrimaryColor != null -> {
            val customColorSet = remember(customPrimaryColor) { generateColorSetFromPrimary(customPrimaryColor) }
            if (darkTheme) customColorSet.darkColorScheme else customColorSet.lightColorScheme
        }
        else -> {
            val palette = when (themeColor) {
                ThemeColor.Blue -> ColorPalettes.Blue
                ThemeColor.Purple -> ColorPalettes.Purple
                ThemeColor.Golden -> ColorPalettes.Golden
                else -> ColorPalettes.Green // Green, Dynamic fallback, Custom fallback
            }
            if(darkTheme) palette.darkColorScheme else palette.lightColorScheme
        }
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
