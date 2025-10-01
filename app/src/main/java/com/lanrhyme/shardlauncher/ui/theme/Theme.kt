package com.lanrhyme.shardlauncher.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeColor(val title: String) {
    Green("绿色"),
    Blue("蓝色"),
    Purple("紫色")
}

data class ColorSet(
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
)

object ColorPalettes {
    val Green = ColorSet(
        lightColorScheme = lightColorScheme(
            primary = Green40,
            secondary = GreenGrey40,
            tertiary = GreenPink40,
            error = ErrorColor
        ),
        darkColorScheme = darkColorScheme(
            primary = Green80,
            secondary = GreenGrey80,
            tertiary = GreenPink80,
            background = DarkBackgroundColor,
            surface = DarkSurfaceColor,
            error = ErrorColor
        )
    )

    val Blue = ColorSet(
        lightColorScheme = lightColorScheme(
            primary = Blue40,
            secondary = BlueGrey40,
            tertiary = BluePink40,
            error = ErrorColor
        ),
        darkColorScheme = darkColorScheme(
            primary = Blue80,
            secondary = BlueGrey80,
            tertiary = BluePink80,
            background = DarkBackgroundColor,
            surface = DarkSurfaceColor,
            error = ErrorColor
        )
    )

    val Purple = ColorSet(
        lightColorScheme = lightColorScheme(
            primary = Purple40,
            secondary = PurpleGrey40,
            tertiary = Pink40,
            error = ErrorColor
        ),
        darkColorScheme = darkColorScheme(
            primary = Purple80,
            secondary = PurpleGrey80,
            tertiary = Pink80,
            background = DarkBackgroundColor,
            surface = DarkSurfaceColor,
            error = ErrorColor
        )
    )
}

@Composable
fun ShardLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: ThemeColor = ThemeColor.Green, // Default to Green
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeColor) {
        ThemeColor.Green -> if (darkTheme) ColorPalettes.Green.darkColorScheme else ColorPalettes.Green.lightColorScheme
        ThemeColor.Blue -> if (darkTheme) ColorPalettes.Blue.darkColorScheme else ColorPalettes.Blue.lightColorScheme
        ThemeColor.Purple -> if (darkTheme) ColorPalettes.Purple.darkColorScheme else ColorPalettes.Purple.lightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
