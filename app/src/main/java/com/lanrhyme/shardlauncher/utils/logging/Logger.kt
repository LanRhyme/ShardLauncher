package com.lanrhyme.shardlauncher.utils.logging

import android.content.Context
import android.util.Log
import com.lanrhyme.shardlauncher.path.PathManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

/**
 * [Modified from HMCL](https://github.com/HMCL-dev/HMCL/blob/57018bef47417108b75e2298ab61f89a7586b1b9/HMCLCore/src/main/java/org/jackhuang/hmcl/util/logging/Logger.java)
 */
object Logger : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()

    private lateinit var PACKAGE_PREFIX: String
    private val isInitialized = AtomicBoolean(false)
    private val channel = Channel<LogMessage>(Channel.UNLIMITED)

    private val logRetentionDays = 7 // Default to 7 days for now

    private var currentLogFile: File? = null
    private var logWriter: PrintWriter? = null
    private var inMemoryLogs: ByteArrayOutputStream? = null

    /**
     * 初始化日志
     */
    fun initialize(context: Context) {
        PACKAGE_PREFIX = "${context.packageName}."

        if (!isInitialized.compareAndSet(false, true)) return

        launch(Dispatchers.IO) {
            //由于安卓不存在“退出”这种设置
            //所以清理旧的日志的工作需要放到初始化阶段
            deleteOldLogs()
            setupLogWriter()
            processEvents()
        }
    }

    private suspend fun setupLogWriter() = withContext(Dispatchers.IO) {
        try {
            currentLogFile = createLogFile()
            logWriter = PrintWriter(currentLogFile!!.writer())
        } catch (e: IOException) {
            val logMessage = LogMessage(
                System.currentTimeMillis(),
                "Logger.setupLogWriter",
                Level.WARNING,
                "Failed to create log file", e
            )
            runBlocking { channel.send(logMessage) }
            inMemoryLogs = ByteArrayOutputStream(1024 * 1024) // 1MB buffer
            logWriter = PrintWriter(inMemoryLogs!!)
        }
    }

    private fun createLogFile(): File {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss", Locale.US)
        var file: File
        var counter = 0

        do {
            val suffix = if (counter == 0) "" else ".$counter"
            file = File(PathManager.DIR_LAUNCHER_LOGS, "log_${dateFormat.format(Date())}$suffix.log")
            counter++
        } while (file.exists())

        file.createNewFile()
        return file
    }

    private suspend fun processEvents() = withContext(Dispatchers.IO) {
        for (message in channel) {
            handleLogMessage(message)
        }
    }

    private fun handleLogMessage(message: LogMessage) {
        val formatted = formatMessage(message)

        //输出到 Logcat
        printToLogcat(message.level, formatted)

        logWriter?.apply {
            println(formatted)
            message.throwable?.also { th ->
                th.printStackTrace(this@apply)
                printToLogcat(message.level, th)
            }
            flush()
        }
    }

    private fun printToLogcat(level: Level, message: String) {
        when (level) {
            Level.ERROR -> Log.e("AppLog", message)
            Level.WARNING -> Log.w("AppLog", message)
            Level.INFO -> Log.i("AppLog", message)
            Level.DEBUG -> Log.d("AppLog", message)
            Level.TRACE -> Log.v("AppLog", message)
        }
    }

    private fun printToLogcat(level: Level, throwable: Throwable) {
        val thMessage = Log.getStackTraceString(throwable)

        when (level) {
            Level.ERROR -> Log.e("AppLog", thMessage)
            Level.WARNING -> Log.w("AppLog", thMessage)
            Level.INFO -> Log.i("AppLog", thMessage)
            Level.DEBUG -> Log.d("AppLog", thMessage)
            Level.TRACE -> Log.v("AppLog", thMessage)
        }
    }

    private fun formatMessage(message: LogMessage): String {
        val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(message.time)
        val caller = message.caller?.let {
            if (it.startsWith(PACKAGE_PREFIX)) "~${it.substring(PACKAGE_PREFIX.length)}" else it
        } ?: "Unknown"

        return buildString {
            append("[$time] [")
            append(caller)
            append("/")
            append(message.level.name)
            append("] ")
            append(message.message)
        }
    }

    private suspend fun deleteOldLogs() = withContext(Dispatchers.IO) {
        PathManager.DIR_LAUNCHER_LOGS.listFiles()?.let { files ->
            val cutoff = System.currentTimeMillis() - logRetentionDays * 86400000L
            files.filter {
                //过滤出日期超过指定天数的日志文件
                it.lastModified() < cutoff
            }.forEach {
                FileUtils.deleteQuietly(it)
            }
        }
    }

    fun lError(msg: String, t: Throwable? = null) =
        log(Level.ERROR, findCaller(), msg, t)

    fun lWarning(msg: String, t: Throwable? = null) =
        log(Level.WARNING, findCaller(), msg, t)

    fun lInfo(msg: String, t: Throwable? = null) =
        log(Level.INFO, findCaller(), msg, t)

    fun lDebug(msg: String, t: Throwable? = null) =
        log(Level.DEBUG, findCaller(), msg, t)

    fun lTrace(msg: String, t: Throwable? = null) =
        log(Level.TRACE, findCaller(), msg, t)

    /**
     * 输出日志
     */
    fun log(level: Level, caller: String?, message: String, throwable: Throwable? = null) {
        if (!isInitialized.get()) return

        val logMessage = LogMessage(
            time = System.currentTimeMillis(),
            caller = caller,
            level = level,
            message = message,
            throwable = throwable
        )

        launch {
            channel.send(logMessage)
        }
    }

    /**
     * 找到调用者
     */
    private fun findCaller(): String? {
        return Throwable().stackTrace.firstOrNull { element ->
            element.className != this::class.java.name &&
                    !element.className.startsWith("kotlin.coroutines") &&
                    !element.className.startsWith("kotlinx.coroutines")
        }?.let {
            val className = it.className.substringAfterLast('.')
            "$className.${it.methodName}"
        }
    }
}