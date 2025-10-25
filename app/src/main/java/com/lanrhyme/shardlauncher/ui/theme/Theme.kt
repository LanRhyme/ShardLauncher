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
}

data class ColorSet(
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
)

object ColorPalettes {
    val Green = ColorSet(
        lightColorScheme = lightColorScheme(
            primary = lightGreen,
            secondary = lightGreenGrey,
            tertiary = lightGreenTertiary,
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
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        themeColor == ThemeColor.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> when (themeColor) {
            ThemeColor.Green -> ColorPalettes.Green.darkColorScheme
            ThemeColor.Blue -> ColorPalettes.Blue.darkColorScheme
            ThemeColor.Purple -> ColorPalettes.Purple.darkColorScheme
            ThemeColor.Golden -> ColorPalettes.Golden.darkColorScheme
            ThemeColor.Dynamic -> ColorPalettes.Green.darkColorScheme // Fallback for older versions
        }
        else -> when (themeColor) {
            ThemeColor.Green -> ColorPalettes.Green.lightColorScheme
            ThemeColor.Blue -> ColorPalettes.Blue.lightColorScheme
            ThemeColor.Purple -> ColorPalettes.Purple.lightColorScheme
            ThemeColor.Golden -> ColorPalettes.Golden.lightColorScheme
            ThemeColor.Dynamic -> ColorPalettes.Green.lightColorScheme // Fallback for older versions
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
