package com.lanrhyme.shardlauncher.ui.custom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast

fun handleEvent(context: Context, eventType: String?, eventData: String?) {
    if (eventType == null) return

    Log.d("XamlEvent", "Handling event: $eventType with data: $eventData")

    when (eventType) {
        "打开网页" -> {
            eventData?.let {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("XamlEvent", "Failed to open URL: $it", e)
                    Toast.makeText(context, "无法打开链接", Toast.LENGTH_SHORT).show()
                }
            }
        }
        "弹出窗口" -> {
            val parts = eventData?.split('|', limit = 2) ?: listOf()
            val title = parts.getOrNull(0) ?: ""
            val message = parts.getOrNull(1) ?: ""
            // TODO: Replace with a proper dialog
            Toast.makeText(context, "$title\n$message", Toast.LENGTH_LONG).show()
        }
        "启动游戏" -> {
            Toast.makeText(context, "启动游戏: $eventData", Toast.LENGTH_SHORT).show()
        }
        "下载文件" -> {
            Toast.makeText(context, "下载文件: $eventData", Toast.LENGTH_SHORT).show()
        }
        "执行命令" -> {
            Toast.makeText(context, "执行命令: $eventData", Toast.LENGTH_SHORT).show()
        }
        "复制文本" -> {
            Toast.makeText(context, "复制文本: $eventData", Toast.LENGTH_SHORT).show()
        }
        else -> {
            Log.w("XamlEvent", "Unknown event type: $eventType")
            Toast.makeText(context, "未知操作: $eventType", Toast.LENGTH_SHORT).show()
        }
    }
}
