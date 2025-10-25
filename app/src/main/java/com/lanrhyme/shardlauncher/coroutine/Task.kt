package com.lanrhyme.shardlauncher.coroutine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.UUID

class Task private constructor(
    val id: String,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    val task: suspend CoroutineScope.(Task) -> Unit,
    val onError: suspend (Throwable) -> Unit = {},
    val onFinally: () -> Unit = {},
    val onCancel: () -> Unit = {}
) {
    /**
     * 任务阶段（TaskSystem可能用不到，主要服务于GameInstaller）
     */
    var taskState by mutableStateOf(TaskState.PREPARING)

    var currentProgress by mutableFloatStateOf(-1f)
        private set
    var currentMessageRes by mutableStateOf<Int?>(null)
        private set
    var currentMessageArgs by mutableStateOf<Array<out Any>?>(null)
        private set

    /**
     * 更新进度，自动处理 NaN、isInfinite 的这种错误情况
     * @param percentage 进度百分比，-1f代表进度不确定
     */
    fun updateProgress(percentage: Float) {
        this.currentProgress = (percentage.takeIf { it.isFinite() } ?: 0f).coerceIn(-1f, 1f)
    }

    /**
     * 更新进度、任务描述消息
     * @param percentage 进度百分比，-1f代表进度不确定，自动处理 NaN、isInfinite 的这种错误情况
     * @param message 任务描述消息
     */
    fun updateProgress(percentage: Float, message: Int?) {
        this.updateProgress(percentage = percentage)
        this.updateMessage(message = message)
    }

    /**
     * 更新进度、任务描述消息
     * @param percentage 进度百分比，-1f代表进度不确定，自动处理 NaN、isInfinite 的这种错误情况
     * @param message 任务描述消息
     * @param args 任务描述信息格式化内容
     */
    fun updateProgress(percentage: Float, message: Int?, vararg args: Any) {
        this.updateProgress(percentage = percentage)
        this.updateMessage(message = message, args = args)
    }

    /**
     * 更新任务描述消息
     * @param message 任务描述消息
     */
    fun updateMessage(message: Int?) {
        this.currentMessageRes = message
        this.currentMessageArgs = null
    }

    /**
     * 更新任务描述消息
     * @param message 任务描述消息
     * @param args 任务描述信息格式化内容
     */
    fun updateMessage(message: Int?, vararg args: Any) {
        this.currentMessageRes = message
        this.currentMessageArgs = args
    }

    override fun equals(other: Any?): Boolean = other is Task && other.id == this.id

    override fun hashCode(): Int = id.hashCode()

    companion object {
        fun runTask(
            id: String? = null,
            dispatcher: CoroutineDispatcher = Dispatchers.Default,
            task: suspend CoroutineScope.(Task) -> Unit,
            onError: suspend (Throwable) -> Unit = {},
            onFinally: () -> Unit = {},
            onCancel: () -> Unit = {}
        ): Task =
            Task(
                id = id ?: getRandomID(),
                dispatcher = dispatcher,
                task = task,
                onError = onError,
                onFinally = onFinally,
                onCancel = onCancel
            )

        private fun getRandomID(): String = UUID.randomUUID().toString()
    }
}