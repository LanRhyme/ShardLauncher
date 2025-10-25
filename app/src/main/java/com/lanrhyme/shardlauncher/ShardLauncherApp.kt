package com.lanrhyme.shardlauncher

import android.app.Application
import android.content.Intent
import com.lanrhyme.shardlauncher.ui.crash.CrashActivity
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class ShardLauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        com.lanrhyme.shardlauncher.path.PathManager.refreshPaths(this)
        com.lanrhyme.shardlauncher.utils.logging.Logger.initialize(this)
        setCrashHandler()
    }

    private fun setCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val stringWriter = StringWriter()
            throwable.printStackTrace(PrintWriter(stringWriter))
            val errorLog = stringWriter.toString()

            val intent = Intent(this, CrashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(CrashActivity.EXTRA_LOG, errorLog)
            }
            startActivity(intent)

            // Exit the app
            defaultHandler?.uncaughtException(thread, throwable)
            exitProcess(1)
        }
    }
}