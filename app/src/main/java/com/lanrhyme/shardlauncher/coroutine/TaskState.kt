package com.lanrhyme.shardlauncher.coroutine

enum class TaskState {
    /** 预备 */
    PREPARING,
    /** 运行中 */
    RUNNING,
    /** 已完成 */
    COMPLETED
}