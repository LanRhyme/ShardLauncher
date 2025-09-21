// ui/theme/Color.kt
package com.lanrhyme.shardlauncher.ui.theme // 替换为你的包名

import androidx.compose.ui.graphics.Color

// Light Theme Colors (Green Palette from your website)
val LightGreenPrimary = Color(0xFF28A745) // --amethyst-color
val LightGreenOnPrimary = Color.White // Text on primary
val LightGreenPrimaryContainer = Color(0xFFA5D6A7) // A lighter shade for containers
val LightGreenOnPrimaryContainer = Color(0xFF1B5E20) // Text on primary container

val LightGreenSecondary = Color(0xFF00796B) // A complementary secondary, adjust as needed
val LightGreenOnSecondary = Color.White
val LightGreenSecondaryContainer = Color(0xFFB2DFDB)
val LightGreenOnSecondaryContainer = Color(0xFF004D40)

val LightGreenTertiary = Color(0xFF388E3C) // Another accent, adjust as needed
val LightGreenOnTertiary = Color.White
val LightGreenTertiaryContainer = Color(0xFFC8E6C9)
val LightGreenOnTertiaryContainer = Color(0xFF1B5E20)

val LightGreenError = Color(0xFFB00020)
val LightGreenOnError = Color.White
val LightGreenErrorContainer = Color(0xFFFCD8DF)
val LightGreenOnErrorContainer = Color(0xFFB00020)

val LightBackground = Color(0xFFF8F9FA) // --bg-primary
val LightOnBackground = Color(0xFF212529) // --text-primary
val LightSurface = Color(0xFFFFFFFF)    // --card-bg
val LightOnSurface = Color(0xFF212529)   // --text-primary
val LightSurfaceVariant = Color(0xFFE9ECEF) // --card-border or a slightly different surface
val LightOnSurfaceVariant = Color(0xFF495057) // Secondary text
val LightOutline = Color(0xFFE0E0E0)      // Border color

// Dark Theme Colors (Green Palette from your website)
val DarkGreenPrimary = Color(0xFF3DDC84)  // --amethyst-color (dark)
val DarkGreenOnPrimary = Color(0xFF121212) // Text on primary (dark)
val DarkGreenPrimaryContainer = Color(0xFF2E7D32) // A darker shade for containers (dark)
val DarkGreenOnPrimaryContainer = Color(0xFFC8E6C9) // Text on primary container (dark)

val DarkGreenSecondary = Color(0xFF4DB6AC) // A complementary secondary (dark), adjust
val DarkGreenOnSecondary = Color(0xFF121212)
val DarkGreenSecondaryContainer = Color(0xFF00695C)
val DarkGreenOnSecondaryContainer = Color(0xFFB2DFDB)

val DarkGreenTertiary = Color(0xFF66BB6A) // Another accent (dark), adjust
val DarkGreenOnTertiary = Color(0xFF121212)
val DarkGreenTertiaryContainer = Color(0xFF2E7D32)
val DarkGreenOnTertiaryContainer = Color(0xFFC8E6C9)

val DarkGreenError = Color(0xFFCF6679)
val DarkGreenOnError = Color(0xFF121212)
val DarkGreenErrorContainer = Color(0xFFF6BDC0)
val DarkGreenOnErrorContainer = Color(0xFFCF6679)

val DarkBackground = Color(0xFF121212) // --bg-primary (dark)
val DarkOnBackground = Color(0xFFE9ECEF) // --text-primary (dark)
val DarkSurface = Color(0xFF1E1E1E)     // --card-bg (dark)
val DarkOnSurface = Color(0xFFE9ECEF)    // --text-primary (dark)
val DarkSurfaceVariant = Color(0xFF343A40) // --card-border (dark) or a slightly different surface
val DarkOnSurfaceVariant = Color(0xFFADB5BD) // Secondary text (dark)
val DarkOutline = Color(0xFF424242)       // Border color (dark)

// Seed color for theme generation (optional, Material 3 builder uses this)
// val seed = Color(0xFF28A745) // Your primary green
