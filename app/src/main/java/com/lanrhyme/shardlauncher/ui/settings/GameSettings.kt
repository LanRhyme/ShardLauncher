
package com.lanrhyme.shardlauncher.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameSettingsContent(
    useBmclapi: Boolean,
    onUseBmclapiChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SwitchSettingItem(
            title = "优先使用 BMCLAPI",
            summary = "开启后将优先从 BMCLAPI 下载游戏版本，可以加快下载速度",
            checked = useBmclapi,
            onCheckedChange = onUseBmclapiChange
        )
    }
}
