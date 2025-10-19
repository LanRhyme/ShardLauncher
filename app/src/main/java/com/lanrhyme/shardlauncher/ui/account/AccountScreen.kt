package com.lanrhyme.shardlauncher.ui.account

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lanrhyme.shardlauncher.R
import com.lanrhyme.shardlauncher.model.Account
import com.lanrhyme.shardlauncher.model.AccountType
import com.lanrhyme.shardlauncher.ui.theme.ShardLauncherTheme

@Composable
fun AccountScreen(navController: NavController) {
    // Sample data for demonstration
    val accounts = listOf(
        Account("1", "LongPlayerName_12345", AccountType.ONLINE, "2 hours ago", "https://crafatar.com/avatars/b8a7c7c8-f6e-4b9b-8a7c-7c8f6e6b9b8a"),
        Account("2", "Steve", AccountType.OFFLINE, "1 day ago", "https://crafatar.com/avatars/8667ba71-b85a-4004-af54-457a9734eed7"),
        Account("3", "Player3", AccountType.ONLINE, "A very very long time ago", "https://crafatar.com/avatars/c8f6e6b9-b8a7-4c7c-8f6e-6b9b8a7c7c8f"),
        Account("4", "Alex", AccountType.ONLINE, "5 hours ago", "https://crafatar.com/avatars/61699b2e-d327-421c-9f4c-c63a442838e6"),
        Account("5", "Herobrine", AccountType.OFFLINE, "1 year ago", "https://crafatar.com/avatars/f868c14a-5282-4598-a83d-3a52140e6376"),
        Account("6", "Notch", AccountType.ONLINE, "10 years ago", "https://crafatar.com/avatars/069a79f4-44e9-4726-a5be-fca90e38aaf5")
    )
    var selectedAccount by remember { mutableStateOf<Account?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("账户档案", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            ShardAccountCard()
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Left side: Large card for the 3D model placeholder
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.3f)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "模型", textAlign = TextAlign.Center)
                }
            }

            // Right side: Horizontally scrollable grid of account cards
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2), // 2 rows, so it forms 2 cards per column
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.7f),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(accounts) { account ->
                    AccountCard(
                        account = account,
                        isSelected = selectedAccount == account,
                        onClick = { selectedAccount = account }
                    )
                }
            }
        }
    }
}

@Composable
fun ShardAccountCard() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.img_lanrhyme),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text("LanRhyme", style = MaterialTheme.typography.bodyLarge)
            Text("ShardAccount", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountCard(account: Account, isSelected: Boolean, onClick: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    val cardWidth = 120.dp

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card-scale"
    )

    val border = if (isSelected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        null
    }

    Box {
        Card(
            modifier = Modifier
                .scale(scale)
                .width(cardWidth)
                .fillMaxHeight()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { showMenu = true }
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = border
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(account.skinUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Account Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f)
                )

                // Info Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly // Distribute text evenly
                ) {
                    Text(
                        text = account.username,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = account.accountType.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("删除账户档案") },
                onClick = {
                    // TODO: Handle delete action
                    showMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("保存皮肤") },
                onClick = {
                    // TODO: Handle save skin action
                    showMenu = false
                }
            )
            if (account.accountType == AccountType.OFFLINE) {
                DropdownMenuItem(
                    text = { Text("修改用户名") },
                    onClick = {
                        // TODO: Handle modify username action
                        showMenu = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 720)
@Composable
fun AccountScreenPreview() {
    ShardLauncherTheme {
        AccountScreen(navController = rememberNavController())
    }
}
