package com.lanrhyme.shardlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
// import androidx.compose.foundation.layout.PaddingValues // Not explicitly used, can be removed if not needed elsewhere
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

// 导入浅色主题颜色定义
import com.lanrhyme.shardlauncher.ui.theme.LightGreenPrimary
import com.lanrhyme.shardlauncher.ui.theme.LightGreenOnPrimary
import com.lanrhyme.shardlauncher.ui.theme.LightGreenPrimaryContainer
import com.lanrhyme.shardlauncher.ui.theme.LightGreenOnPrimaryContainer
import com.lanrhyme.shardlauncher.ui.theme.LightGreenSecondary
import com.lanrhyme.shardlauncher.ui.theme.LightGreenOnSecondary
import com.lanrhyme.shardlauncher.ui.theme.LightGreenSecondaryContainer
import com.lanrhyme.shardlauncher.ui.theme.LightGreenOnSecondaryContainer
import com.lanrhyme.shardlauncher.ui.theme.LightGreenTertiary
import com.lanrhyme.shardlauncher.ui.theme.LightGreenOnTertiary
import com.lanrhyme.shardlauncher.ui.theme.LightGreenTertiaryContainer
import com.lanrhyme.shardlauncher.ui.theme.LightGreenOnTertiaryContainer
import com.lanrhyme.shardlauncher.ui.theme.LightGreenError
import com.lanrhyme.shardlauncher.ui.theme.LightGreenOnError
import com.lanrhyme.shardlauncher.ui.theme.LightGreenErrorContainer
import com.lanrhyme.shardlauncher.ui.theme.LightGreenOnErrorContainer
import com.lanrhyme.shardlauncher.ui.theme.LightBackground
import com.lanrhyme.shardlauncher.ui.theme.LightOnBackground
import com.lanrhyme.shardlauncher.ui.theme.LightSurface
import com.lanrhyme.shardlauncher.ui.theme.LightOnSurface
import com.lanrhyme.shardlauncher.ui.theme.LightSurfaceVariant
import com.lanrhyme.shardlauncher.ui.theme.LightOnSurfaceVariant
import com.lanrhyme.shardlauncher.ui.theme.LightOutline

// 导入深色主题颜色定义
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenPrimary
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenOnPrimary
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenPrimaryContainer
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenOnPrimaryContainer
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenSecondary
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenOnSecondary
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenSecondaryContainer
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenOnSecondaryContainer
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenTertiary
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenOnTertiary
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenTertiaryContainer
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenOnTertiaryContainer
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenError
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenOnError
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenErrorContainer
import com.lanrhyme.shardlauncher.ui.theme.DarkGreenOnErrorContainer
import com.lanrhyme.shardlauncher.ui.theme.DarkBackground
import com.lanrhyme.shardlauncher.ui.theme.DarkOnBackground
import com.lanrhyme.shardlauncher.ui.theme.DarkSurface
import com.lanrhyme.shardlauncher.ui.theme.DarkOnSurface
import com.lanrhyme.shardlauncher.ui.theme.DarkSurfaceVariant
import com.lanrhyme.shardlauncher.ui.theme.DarkOnSurfaceVariant
import com.lanrhyme.shardlauncher.ui.theme.DarkOutline

// 导入新的 SettingsPage
import com.lanrhyme.shardlauncher.ui.settings.SettingsPage // 确保这个导入是正确的


sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "主页", Icons.Filled.Home)
    object Settings : Screen("settings", "设置", Icons.Filled.Settings)
}

val navigationItems = listOf(
    Screen.Home,
    Screen.Settings
)

// 定义浅色主题配色
private val LightColors = lightColorScheme(
    primary = LightGreenPrimary,
    onPrimary = LightGreenOnPrimary,
    primaryContainer = LightGreenPrimaryContainer,
    onPrimaryContainer = LightGreenOnPrimaryContainer,
    secondary = LightGreenSecondary,
    onSecondary = LightGreenOnSecondary,
    secondaryContainer = LightGreenSecondaryContainer,
    onSecondaryContainer = LightGreenOnSecondaryContainer,
    tertiary = LightGreenTertiary,
    onTertiary = LightGreenOnTertiary,
    tertiaryContainer = LightGreenTertiaryContainer,
    onTertiaryContainer = LightGreenOnTertiaryContainer,
    error = LightGreenError,
    onError = LightGreenOnError,
    errorContainer = LightGreenErrorContainer,
    onErrorContainer = LightGreenOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

// 定义深色主题配色
private val DarkColors = darkColorScheme(
    primary = DarkGreenPrimary,
    onPrimary = DarkGreenOnPrimary,
    primaryContainer = DarkGreenPrimaryContainer,
    onPrimaryContainer = DarkGreenOnPrimaryContainer,
    secondary = DarkGreenSecondary,
    onSecondary = DarkGreenOnSecondary,
    secondaryContainer = DarkGreenSecondaryContainer,
    onSecondaryContainer = DarkGreenOnSecondaryContainer,
    tertiary = DarkGreenTertiary,
    onTertiary = DarkGreenOnTertiary,
    tertiaryContainer = DarkGreenTertiaryContainer,
    onTertiaryContainer = DarkGreenOnTertiaryContainer,
    error = DarkGreenError,
    onError = DarkGreenOnError,
    errorContainer = DarkGreenErrorContainer,
    onErrorContainer = DarkGreenOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val systemIsDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemIsDark) }

            AppTheme(darkTheme = isDarkTheme) {
                MainScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

@Composable
fun AppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp)
        ),
        content = content
    )
}

@Composable
fun MainScreen(isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    val navController = rememberNavController()
    var isSidebarExpanded by remember { mutableStateOf(false) }
    val sidebarWidth by animateDpAsState(
        targetValue = if (isSidebarExpanded) 200.dp else 70.dp,
        animationSpec = tween(durationMillis = 300)
    )
    val contentBlurRadius by animateDpAsState(
        targetValue = if (isSidebarExpanded) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            // ---- START: 临时添加的 Box 用于测试 ---
            Box(
                modifier = Modifier
                    .width(sidebarWidth)
                    .fillMaxHeight()
                    .background(Color(0xFFFF00FF)) // Magenta color for testing
            ) {
                SideBar(
                    modifier = Modifier.fillMaxSize(), // 让 SideBar 填满这个 Magenta Box
                    isExpanded = isSidebarExpanded,
                    onToggleExpand = { isSidebarExpanded = !isSidebarExpanded },
                    navController = navController
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(radius = contentBlurRadius)
                        .clickable(enabled = isSidebarExpanded) {
                            if (isSidebarExpanded) {
                                isSidebarExpanded = false
                            }
                        }
                ) {
                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) { HomePage() }
                        composable(Screen.Settings.route) {
                            SettingsPage( // 调用导入的 SettingsPage
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = onThemeToggle
                            )
                        }
                    }
                }

                if (isSidebarExpanded && contentBlurRadius > 0.dp) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable { isSidebarExpanded = false }
                    )
                }
            }
        }
    }
}

@Composable
fun SideBar(
    modifier: Modifier = Modifier, // 这个 modifier 仍然是 .fillMaxSize() (来自 Magenta Box)
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    navController: NavController
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    Surface(
        modifier = modifier // Surface 填满其分配的空间 (Magenta Box)
            .clip(RoundedCornerShape(16.dp)), // Surface 本身应用圆角裁剪
        shape = RoundedCornerShape(16.dp),   // Surface 的形状也是圆角
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        tonalElevation = 0.dp
    ) {
        Column(            modifier = Modifier
            .fillMaxHeight()
            .padding(all = 8.dp) // <--- 将 padding 移到 Column 这里
            .padding(vertical = 16.dp, horizontal = 4.dp), // 您原有的内部 Column padding,可以根据需要调整或合并
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onToggleExpand,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = if (isExpanded) "Collapse Sidebar" else "Expand Sidebar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            navigationItems.forEach { screen ->
                SideBarButton(
                    screen = screen,
                    isExpanded = isExpanded,
                    isSelected = currentScreen.route == screen.route,
                    onClick = {
                        if (currentScreen.route != screen.route) {
                            currentScreen = screen
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun SideBarButton(
    screen: Screen,
    isExpanded: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.7f
        )
    val shape = RoundedCornerShape(12.dp)

    val buttonModifier = if (isExpanded) {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    } else {
        Modifier.size(48.dp)
    }

    Box(
        modifier = Modifier
            .then(buttonModifier)
            .height(56.dp)
            .clip(shape)
            .background(backgroundColor, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            if (isExpanded) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = screen.label,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun HomePage() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("这是主页页面", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

// SettingsPage Composable is now correctly in com.lanrhyme.shardlauncher.ui.settings.SettingsScreen.kt

