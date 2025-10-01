package com.lanrhyme.shardlauncher.ui.crash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lanrhyme.shardlauncher.ui.theme.ShardLauncherTheme

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val errorLog = intent.getStringExtra(EXTRA_LOG) ?: "No error log available."
        setContent {
            ShardLauncherTheme {
                CrashScreen(errorLog = errorLog)
            }
        }
    }

    companion object {
        const val EXTRA_LOG = "com.lanrhyme.shardlauncher.EXTRA_LOG"
    }
}