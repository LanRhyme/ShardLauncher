package com.lanrhyme.shardlauncher.utils

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Logger {
    private const val LOG_FILE_NAME = "leastlog.txt"

    fun log(context: Context, tag: String, message: String) {
        try {
            val logFile = File(context.getExternalFilesDir(null), LOG_FILE_NAME)
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            FileWriter(logFile, true).use {
                it.append("$timestamp [$tag]: $message\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
