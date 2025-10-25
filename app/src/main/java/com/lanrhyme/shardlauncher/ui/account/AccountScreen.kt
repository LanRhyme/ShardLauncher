package com.lanrhyme.shardlauncher.ui.account

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.lanrhyme.shardlauncher.R
import com.lanrhyme.shardlauncher.model.Account
import com.lanrhyme.shardlauncher.ui.theme.ShardLauncherTheme

@Composable
fun AccountScreen(navController: NavController, accountViewModel: AccountViewModel = viewModel()) {
    val accounts by accountViewModel.accounts.collectAsState()
    val selectedAccount by accountViewModel.selectedAccount.collectAsState()
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var editingAccount by remember { mutableStateOf<Account?>(null) }
    val microsoftLoginState by accountViewModel.microsoftLoginState.collectAsState()

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
            IconButton(onClick = { showAddAccountDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Account")
            }
            ShardAccountCard()
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Left side: Large card for the 3D model placeholder
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.3f)
                    .padding(16.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    selectedAccount?.let { account ->
                        val imageRequest = ImageRequest.Builder(LocalContext.current)
                            .data("https://api.xingzhige.com/API/get_Minecraft_skins/?name=${account.username}&type=身体&overlay=true")
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.img_lanrhyme)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build()
                        SubcomposeAsyncImage(
                            model = imageRequest,
                            contentDescription = "${account.username}'s skin",
                            modifier = Modifier.fillMaxSize(0.8f),
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                }
                            }
                        )
                    } ?: run {
                        Text(text = "未选择账户", textAlign = TextAlign.Center)
                    }
                }
            }

            // Right side: Horizontally scrollable grid of account cards
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
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
                        onClick = { accountViewModel.selectAccount(account) },
                        onDelete = { accountViewModel.deleteAccount(it) },
                        onEdit = { editingAccount = it },
                        navController = navController
                    )
                }
            }
        }
    }

    if (showAddAccountDialog) {
        AddAccountDialog(
            onDismiss = { showAddAccountDialog = false },
            onAddOfflineAccount = {
                accountViewModel.addOfflineAccount(it)
                showAddAccountDialog = false
            },
            onLoginWithMicrosoft = { accountViewModel.loginWithMicrosoft() }
        )
    }

    when (val state = microsoftLoginState) {
        is MicrosoftLoginState.InProgress -> {
            val clipboardManager = LocalClipboardManager.current
            AlertDialog(
                onDismissRequest = { accountViewModel.resetMicrosoftLoginState() },
                title = { Text("验证设备") },
                text = {
                    Column {
                        Text("请在浏览器中打开以下链接，并输入验证码：")
                        Text(state.deviceCodeResponse.verificationUri)
                        TextButton(onClick = { clipboardManager.setText(AnnotatedString(state.deviceCodeResponse.userCode)) }) {
                            Text(state.deviceCodeResponse.userCode)
                        }
                    }
                 },
                confirmButton = { TextButton(onClick = { accountViewModel.resetMicrosoftLoginState() }) { Text("完成") } }
            )
        }
        is MicrosoftLoginState.Error -> {
            AlertDialog(
                onDismissRequest = { accountViewModel.resetMicrosoftLoginState() },
                title = { Text("登录失败") },
                text = { Text(state.message) },
                confirmButton = { TextButton(onClick = { accountViewModel.resetMicrosoftLoginState() }) { Text("确定") } }
            )
        }
        else -> {}
    }

    editingAccount?.let {
        EditAccountDialog(
            account = it,
            onDismiss = { editingAccount = null },
            onConfirm = { newUsername ->
                accountViewModel.updateOfflineAccount(it, newUsername)
                editingAccount = null
            }
        )
    }
}

@Composable
fun AddAccountDialog(
    onDismiss: () -> Unit, 
    onAddOfflineAccount: (String) -> Unit,
    onLoginWithMicrosoft: () -> Unit
) {
    var showOfflineDialog by remember { mutableStateOf(false) }

    if (showOfflineDialog) {
        var username by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("添加离线账户") },
            text = {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { onAddOfflineAccount(username) }
                ) {
                    Text("添加")
                }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("选择账户类型") },
            text = { Text("请选择要添加的账户类型。") },
            confirmButton = {
                OutlinedButton(
                    onClick = { showOfflineDialog = true }
                ) {
                    Text("离线账户")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onLoginWithMicrosoft
                ) {
                    Text("正版账户")
                }
            }
        )
    }
}

@Composable
fun EditAccountDialog(account: Account, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var username by remember { mutableStateOf(account.username) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑账户") },
        text = {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("用户名") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(username) }
            ) {
                Text("保存")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}


@Preview(showBackground = true, widthDp = 1280, heightDp = 720)
@Composable
fun AccountScreenPreview() {
    ShardLauncherTheme {
        AccountScreen(navController = rememberNavController())
    }
}
