package com.lanrhyme.shardlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.lanrhyme.shardlauncher.data.SettingsRepository
import com.lanrhyme.shardlauncher.manager.NotificationManager
import com.lanrhyme.shardlauncher.ui.SplashScreen
import com.lanrhyme.shardlauncher.ui.components.NotificationItem
import com.lanrhyme.shardlauncher.ui.developeroptions.DeveloperOptionsScreen
import com.lanrhyme.shardlauncher.ui.home.HomeScreen
import com.lanrhyme.shardlauncher.ui.model.Notification
import com.lanrhyme.shardlauncher.ui.model.NotificationType
import com.lanrhyme.shardlauncher.ui.navigation.Screen
import com.lanrhyme.shardlauncher.ui.navigation.navigationItems
import com.lanrhyme.shardlauncher.ui.settings.SettingsScreen
import com.lanrhyme.shardlauncher.ui.theme.ShardLauncherTheme
import com.lanrhyme.shardlauncher.ui.theme.ThemeColor
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsRepository = SettingsRepository(applicationContext)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val systemIsDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(settingsRepository.getIsDarkTheme(systemIsDark)) }
            var sidebarPosition by remember { mutableStateOf(settingsRepository.getSidebarPosition()) }
            var themeColor by remember { mutableStateOf(settingsRepository.getThemeColor()) }
            var animationSpeed by remember { mutableStateOf(settingsRepository.getAnimationSpeed()) }

            val navController = rememberNavController()
            var showSplash by remember { mutableStateOf(true) }

            Crossfade(
                targetState = showSplash,
                label = "SplashCrossfade",
                animationSpec = tween(durationMillis = 500)
            ) { show ->
                if (show) {
                    ShardLauncherTheme(darkTheme = isDarkTheme, themeColor = themeColor) {
                        SplashScreen(onAnimationFinished = { showSplash = false })
                    }
                } else {
                    Crossfade(
                        targetState = isDarkTheme,
                        label = "ThemeCrossfade",
                        animationSpec = tween(durationMillis = 500)
                    ) { isDark ->
                        ShardLauncherTheme(darkTheme = isDark, themeColor = themeColor) {
                            MainScreen(
                                navController = navController,
                                isDarkTheme = isDark,
                                onThemeToggle = {
                                    val newTheme = !isDarkTheme
                                    isDarkTheme = newTheme
                                    settingsRepository.setIsDarkTheme(newTheme)
                                },
                                sidebarPosition = sidebarPosition,
                                onPositionChange = { newPosition ->
                                    sidebarPosition = newPosition
                                    settingsRepository.setSidebarPosition(newPosition)
                                },
                                themeColor = themeColor,
                                onThemeColorChange = { newColor ->
                                    themeColor = newColor
                                    settingsRepository.setThemeColor(newColor)
                                },
                                animationSpeed = animationSpeed,
                                onAnimationSpeedChange = { newSpeed ->
                                    animationSpeed = newSpeed
                                    settingsRepository.setAnimationSpeed(newSpeed)
                                }
                            )
                        }
                    }
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
    onThemeColorChange: (ThemeColor) -> Unit,
    animationSpeed: Float,
    onAnimationSpeedChange: (Float) -> Unit
) {
    var isSidebarExpanded by remember { mutableStateOf(false) }
    var showNotificationSidebar by remember { mutableStateOf(false) }

    val sidebarWidth by animateDpAsState(
        targetValue = if (isSidebarExpanded) 220.dp else 72.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = ""
    )

    val contentBlurRadius by animateDpAsState(
        targetValue = if (isSidebarExpanded || showNotificationSidebar) 8.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = ""
    )

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
                onAnimationSpeedChange = onAnimationSpeedChange
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
                position = sidebarPosition,
                onShowNotificationSidebar = { showNotificationSidebar = true }
            )

            NotificationOverlay(
                isDarkTheme = isDarkTheme,
                showSidebar = showNotificationSidebar,
                onDismissSidebar = { showNotificationSidebar = false }
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
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = ""
    )
    val paddingEnd by animateDpAsState(
        targetValue = if (sidebarPosition == SidebarPosition.Right) collapsedSidebarWidth else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = ""
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
                enterTransition = {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val initialIndex = navigationItems.indexOfFirst { it.route == initialRoute }
                    val targetIndex = navigationItems.indexOfFirst { it.route == targetRoute }

                    if (targetRoute == Screen.Home.route) {
                        slideInVertically(animationSpec = tween(animationDuration)) { it } + fadeIn(animationSpec = tween(animationDuration))
                    } else if (initialIndex == -1 || targetIndex == -1) {
                        slideInVertically(animationSpec = tween(animationDuration)) { it } + fadeIn(animationSpec = tween(animationDuration))
                    } else if (targetIndex > initialIndex) {
                        slideInVertically(animationSpec = tween(animationDuration)) { it } + fadeIn(animationSpec = tween(animationDuration))
                    } else {
                        slideInVertically(animationSpec = tween(animationDuration)) { -it } + fadeIn(animationSpec = tween(animationDuration))
                    }
                 },
                exitTransition = {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val initialIndex = navigationItems.indexOfFirst { it.route == initialRoute }
                    val targetIndex = navigationItems.indexOfFirst { it.route == targetRoute }

                    if (targetRoute == Screen.Home.route) {
                        slideOutVertically(animationSpec = tween(animationDuration)) { -it } + fadeOut(animationSpec = tween(animationDuration))
                    } else if (initialIndex == -1 || targetIndex == -1) {
                        slideOutVertically(animationSpec = tween(animationDuration)) { -it } + fadeOut(animationSpec = tween(animationDuration))
                    } else if (targetIndex > initialIndex) {
                        slideOutVertically(animationSpec = tween(animationDuration)) { -it } + fadeOut(animationSpec = tween(animationDuration))
                    } else {
                        slideOutVertically(animationSpec = tween(animationDuration)) { it } + fadeOut(animationSpec = tween(animationDuration))
                    }
                 },
                popEnterTransition = {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val initialIndex = navigationItems.indexOfFirst { it.route == initialRoute }
                    val targetIndex = navigationItems.indexOfFirst { it.route == targetRoute }

                    if (targetRoute == Screen.Home.route) {
                        slideInVertically(animationSpec = tween(animationDuration)) { -it } + fadeIn(animationSpec = tween(animationDuration))
                    } else if (initialIndex == -1 || targetIndex == -1) {
                        slideInVertically(animationSpec = tween(animationDuration)) { -it } + fadeIn(animationSpec = tween(animationDuration))
                    } else if (targetIndex > initialIndex) {
                        slideInVertically(animationSpec = tween(animationDuration)) { -it } + fadeIn(animationSpec = tween(animationDuration))
                    } else {
                        slideInVertically(animationSpec = tween(animationDuration)) { it } + fadeIn(animationSpec = tween(animationDuration))
                    }
                 },
                popExitTransition = {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val initialIndex = navigationItems.indexOfFirst { it.route == initialRoute }
                    val targetIndex = navigationItems.indexOfFirst { it.route == targetRoute }

                    if (targetRoute == Screen.Home.route) {
                        slideOutVertically(animationSpec = tween(animationDuration)) { it } + fadeOut(animationSpec = tween(animationDuration))
                    } else if (initialIndex == -1 || targetIndex == -1) {
                        slideOutVertically(animationSpec = tween(animationDuration)) { it } + fadeOut(animationSpec = tween(animationDuration))
                    } else if (targetIndex > initialIndex) {
                        slideOutVertically(animationSpec = tween(animationDuration)) { it } + fadeOut(animationSpec = tween(animationDuration))
                    } else {
                        slideOutVertically(animationSpec = tween(animationDuration)) { -it } + fadeOut(animationSpec = tween(animationDuration))
                    }
                 }
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Version.route) { VersionScreen() }
                composable(Screen.Download.route) { DownloadScreen() }
                composable(Screen.Online.route) { OnlineScreen() }
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
    position: SidebarPosition,
    onShowNotificationSidebar: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val cardShape = when (position) {
        SidebarPosition.Left -> RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
        SidebarPosition.Right -> RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp)
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(cardShape)
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.62f)
            )
    ) {
        SideBarContent(isExpanded, onToggleExpand, navController, currentRoute, onShowNotificationSidebar)
    }
}

@Composable
private fun SideBarContent(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    navController: NavController,
    currentRoute: String?,
    onShowNotificationSidebar: () -> Unit
) {
    val notifications by NotificationManager.notifications.collectAsState()
    val hasPersistentNotifications = notifications.any { it.type != NotificationType.Temporary }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExpandButton(isExpanded = isExpanded, onClick = onToggleExpand)
        Spacer(modifier = Modifier.height(10.dp))
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
        Spacer(Modifier.weight(1f))
        ExpandButton(isExpanded = false, onClick = onShowNotificationSidebar, icon = Icons.Default.Notifications, showBadge = hasPersistentNotifications)
    }
}

@Composable
fun ExpandButton(
    isExpanded: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    showBadge: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "ExpandButtonScale"
    )
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = isExpanded, label = "ToggleIcon") {
            Icon(
                imageVector = icon ?: if (it) Icons.Filled.ArrowBack else Icons.Filled.Menu,
                contentDescription = if (it) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        if (showBadge) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(8.dp)
                    .background(Color.Red, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun NotificationOverlay(
    isDarkTheme: Boolean,
    showSidebar: Boolean,
    onDismissSidebar: () -> Unit
) {
    val allNotifications by NotificationManager.notifications.collectAsState()
    var popupNotifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    val displayedIds = remember { mutableStateListOf<String>() }

    LaunchedEffect(allNotifications) {
        val newNotifications = allNotifications.filter { it.id !in displayedIds }
        if (newNotifications.isNotEmpty()) {
            popupNotifications = popupNotifications + newNotifications
            newNotifications.forEach { displayedIds.add(it.id) }
        }
    }

    // Popup notifications
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, end = 16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            popupNotifications.forEach { notification ->
                key(notification.id) {
                    PopupNotificationItem(
                        notification = notification,
                        onDismiss = { id, type ->
                            popupNotifications = popupNotifications.filterNot { it.id == id }
                            if (type == NotificationType.Temporary) {
                                NotificationManager.dismiss(id)
                                displayedIds.remove(id)
                            }
                         },
                        darkTheme = isDarkTheme
                    )
                }
            }
        }
    }

    // Notification sidebar scrim
    AnimatedVisibility(
        visible = showSidebar,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onDismissSidebar() }
        )
    }

    // Notification sidebar content
    AnimatedVisibility(
        visible = showSidebar,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.8f)
                    .clickable(enabled = false) {},
                shape = RoundedCornerShape(topStart = 22.dp, bottomStart = 22.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 12.dp
            ) {
                val persistentNotifications = allNotifications.filter { it.type != NotificationType.Temporary }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Notifications", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                    if (persistentNotifications.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No notifications")
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(persistentNotifications) { notification ->
                                NotificationItem(
                                    notification = notification,
                                    onDismiss = { NotificationManager.dismiss(it) },
                                    darkTheme = isDarkTheme,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PopupNotificationItem(
    notification: Notification,
    onDismiss: (String, NotificationType) -> Unit,
    darkTheme: Boolean
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(3000)
        visible = false
        delay(500) // wait for exit animation
        onDismiss(notification.id, notification.type)
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally { it } + fadeIn(),
        exit = slideOutHorizontally { it } + fadeOut()
    ) {
        NotificationItem(
            notification = notification,
            onDismiss = { onDismiss(notification.id, notification.type) },
            darkTheme = darkTheme,
            modifier = Modifier.width(350.dp)
        )
    }
}


@Composable
fun VersionScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("版本", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun DownloadScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("下载", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun OnlineScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("联机", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun SideBarButton(
    screen: Screen,
    isExpanded: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "SidebarButtonScale"
    )

    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.7f
        )
    val shape = RoundedCornerShape(22.dp)

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
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(backgroundColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
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
