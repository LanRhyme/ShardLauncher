package com.lanrhyme.shardlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
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
import com.lanrhyme.shardlauncher.ui.home.HomeScreen
import com.lanrhyme.shardlauncher.ui.navigation.Screen
import com.lanrhyme.shardlauncher.ui.navigation.navigationItems
import com.lanrhyme.shardlauncher.ui.settings.SettingsPage
import com.lanrhyme.shardlauncher.ui.theme.ShardLauncherTheme

enum class SidebarPosition { Left, Right }

class MainActivity : ComponentActivity() {
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

            ShardLauncherTheme(darkTheme = isDarkTheme) {
                MainScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                    sidebarPosition = sidebarPosition,
                    onPositionChange = { newPosition -> sidebarPosition = newPosition }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    sidebarPosition: SidebarPosition,
    onPositionChange: (SidebarPosition) -> Unit
) {
    val navController = rememberNavController()
    var isSidebarExpanded by remember { mutableStateOf(false) }

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
                onPositionChange = onPositionChange
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
    onPositionChange: (SidebarPosition) -> Unit
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
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                enterTransition = { slideInVertically(animationSpec = tween(500)) { it } + fadeIn(animationSpec = tween(500)) },
                exitTransition = { slideOutVertically(animationSpec = tween(500)) { -it } + fadeOut(animationSpec = tween(500)) },
                popEnterTransition = { slideInVertically(animationSpec = tween(500)) { -it } + fadeIn(animationSpec = tween(500)) },
                popExitTransition = { slideOutVertically(animationSpec = tween(500)) { it } + fadeOut(animationSpec = tween(500)) }
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Settings.route) {
                    SettingsPage(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        sidebarPosition = sidebarPosition,
                        onPositionChange = onPositionChange
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

    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = cardShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .fillMaxHeight()
                .fillMaxWidth(),
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
                    isSelected = currentRoute == screen.route,
                    onClick = {
                        if (currentRoute != screen.route) {
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