package com.lanrhyme.shardlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lanrhyme.shardlauncher.common.SidebarPosition
import com.lanrhyme.shardlauncher.ui.developeroptions.DeveloperOptionsScreen
import com.lanrhyme.shardlauncher.ui.home.HomeScreen
import com.lanrhyme.shardlauncher.ui.navigation.Screen
import com.lanrhyme.shardlauncher.ui.navigation.navigationItems
import com.lanrhyme.shardlauncher.ui.settings.SettingsScreen
import com.lanrhyme.shardlauncher.ui.theme.ShardLauncherTheme
import com.lanrhyme.shardlauncher.ui.theme.ThemeColor

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val systemIsDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemIsDark) }
            var sidebarPosition by remember { mutableStateOf(SidebarPosition.Left) }
            var themeColor by remember { mutableStateOf(ThemeColor.Green) }
            val navController = rememberNavController()

            Crossfade(
                targetState = isDarkTheme,
                label = "ThemeCrossfade",
                animationSpec = tween(durationMillis = 500)
            ) { isDark ->
                ShardLauncherTheme(darkTheme = isDark, themeColor = themeColor) {
                    MainScreen(
                        navController = navController,
                        isDarkTheme = isDark,
                        onThemeToggle = { isDarkTheme = !isDarkTheme },
                        sidebarPosition = sidebarPosition,
                        onPositionChange = { newPosition -> sidebarPosition = newPosition },
                        themeColor = themeColor,
                        onThemeColorChange = { newColor -> themeColor = newColor }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    sidebarPosition: SidebarPosition,
    onPositionChange: (SidebarPosition) -> Unit,
    themeColor: ThemeColor,
    onThemeColorChange: (ThemeColor) -> Unit
) {
    var isSidebarExpanded by remember { mutableStateOf(false) }
    var animationSpeed by remember { mutableStateOf(1f) }

    val sidebarWidth by animateDpAsState(
        targetValue = if (isSidebarExpanded) 220.dp else 72.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    val contentBlurRadius by animateDpAsState(
        targetValue = if (isSidebarExpanded) 8.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    // Re-introduce the root Surface to provide a stable, single background
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            MainContent(
                modifier = Modifier.fillMaxSize(),
                isSidebarExpanded = isSidebarExpanded,
                contentBlurRadius = contentBlurRadius,
                onSidebarExpandToggle = { isSidebarExpanded = !isSidebarExpanded },
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                sidebarPosition = sidebarPosition,
                onPositionChange = onPositionChange,
                themeColor = themeColor,
                onThemeColorChange = onThemeColorChange,
                animationSpeed = animationSpeed,
                onAnimationSpeedChange = { animationSpeed = it }
            )

            val sidebarAlignment = when (sidebarPosition) {
                SidebarPosition.Left -> Alignment.CenterStart
                SidebarPosition.Right -> Alignment.CenterEnd
            }

            SideBar(
                modifier = Modifier
                    .align(sidebarAlignment)
                    .width(sidebarWidth),
                isExpanded = isSidebarExpanded,
                onToggleExpand = { isSidebarExpanded = !isSidebarExpanded },
                navController = navController,
                position = sidebarPosition
            )
        }
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    isSidebarExpanded: Boolean,
    contentBlurRadius: androidx.compose.ui.unit.Dp,
    onSidebarExpandToggle: () -> Unit,
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    sidebarPosition: SidebarPosition,
    onPositionChange: (SidebarPosition) -> Unit,
    themeColor: ThemeColor,
    onThemeColorChange: (ThemeColor) -> Unit,
    animationSpeed: Float,
    onAnimationSpeedChange: (Float) -> Unit
) {
    val collapsedSidebarWidth = 72.dp
    val paddingStart by animateDpAsState(
        targetValue = if (sidebarPosition == SidebarPosition.Left) collapsedSidebarWidth else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )
    val paddingEnd by animateDpAsState(
        targetValue = if (sidebarPosition == SidebarPosition.Right) collapsedSidebarWidth else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )
    val contentPadding = PaddingValues(start = paddingStart, end = paddingEnd)

    Box(
        modifier = modifier.blur(radius = contentBlurRadius)
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            val animationDuration = (500 / animationSpeed).toInt()
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                enterTransition = { slideInVertically(animationSpec = tween(animationDuration)) { it } + fadeIn(animationSpec = tween(animationDuration)) },
                exitTransition = { slideOutVertically(animationSpec = tween(animationDuration)) { -it } + fadeOut(animationSpec = tween(animationDuration)) },
                popEnterTransition = { slideInVertically(animationSpec = tween(animationDuration)) { -it } + fadeIn(animationSpec = tween(animationDuration)) },
                popExitTransition = { slideOutVertically(animationSpec = tween(animationDuration)) { it } + fadeOut(animationSpec = tween(animationDuration)) }
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        navController = navController,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        sidebarPosition = sidebarPosition,
                        onPositionChange = onPositionChange,
                        themeColor = themeColor,
                        onThemeColorChange = onThemeColorChange
                    )
                }
                composable(Screen.DeveloperOptions.route) {
                    DeveloperOptionsScreen(
                        animationSpeed = animationSpeed,
                        onAnimationSpeedChange = onAnimationSpeedChange
                    )
                }
            }
        }

        if (isSidebarExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { onSidebarExpandToggle() }
            )
        }
    }
}

@Composable
fun SideBar(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    navController: NavController,
    position: SidebarPosition
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val cardShape = when (position) {
        SidebarPosition.Left -> RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
        SidebarPosition.Right -> RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp)
    }

    val blurRadius = 24.dp
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(cardShape)
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.62f)
            )
    ) {
        SideBarContent(isExpanded, onToggleExpand, navController, currentRoute)
    }
}

@Composable
private fun SideBarContent(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    navController: NavController,
    currentRoute: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onToggleExpand, modifier = Modifier.padding(bottom = 20.dp)) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        navigationItems.forEach { screen ->
            SideBarButton(
                screen = screen,
                isExpanded = isExpanded,
                isSelected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
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
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val shape = RoundedCornerShape(12.dp)

    val buttonModifier = if (isExpanded) {
        Modifier
            .fillMaxWidth()
            .height(56.dp)
    } else {
        Modifier.size(56.dp)
    }

    Box(
        modifier = Modifier
            .then(buttonModifier)
            .clip(shape)
            .background(backgroundColor, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (isExpanded) 14.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.label,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
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
