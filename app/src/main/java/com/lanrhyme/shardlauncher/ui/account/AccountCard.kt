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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lanrhyme.shardlauncher.R
import com.lanrhyme.shardlauncher.model.Account
import com.lanrhyme.shardlauncher.model.AccountType


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
fun AccountCard(account: Account, isSelected: Boolean, onClick: () -> Unit, onLongClick: () -> Unit = {}) {
    var showMenu by remember { mutableStateOf(false) }
    val cardWidth = 120.dp
    val cardHeight = 160.dp

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
                .height(cardHeight)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        showMenu = true
                        onLongClick()
                    }
                ),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
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
