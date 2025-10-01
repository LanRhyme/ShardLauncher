package com.lanrhyme.shardlauncher.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    // The Surface has been removed. This composable is now transparent
    // and will show the background from MainScreen.
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "这是主页页面",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Center)
        )

        Button(
            onClick = { /* TODO: Implement game start logic */ },
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            contentPadding = PaddingValues(horizontal = 35.dp, vertical = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("启动游戏", fontSize = 20.sp)
        }
    }
}
