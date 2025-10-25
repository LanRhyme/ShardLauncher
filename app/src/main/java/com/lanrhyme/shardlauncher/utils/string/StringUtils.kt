package com.lanrhyme.shardlauncher.utils.string

import android.util.Base64
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

fun shiftString(input: String, direction: ShiftDirection, shiftCount: Int): String {
    if (input.isEmpty()) {
        return input
    }

    //确保位移个数在字符串长度范围内
    val length = input.length
    val shiftCount1 = shiftCount % length
    if (shiftCount1 == 0) {
        return input
    }

    return when (direction) {
        ShiftDirection.LEFT -> input.substring(shiftCount1) + input.substring(0, shiftCount1)
        ShiftDirection.RIGHT -> input.substring(length - shiftCount1) + input.substring(0, length - shiftCount1)
    }
}

fun throwableToString(throwable: Throwable): String {
    val stringWriter = StringWriter()
    PrintWriter(stringWriter).use {
        throwable.printStackTrace(it)
    }
    return stringWriter.toString()
}

fun Throwable.getMessageOrToString(): String {
    return message ?: throwableToString(this)
}

fun decodeBase64(rawValue: String): String {
    val decodedBytes = Base64.decode(rawValue, Base64.DEFAULT)
    return String(decodedBytes, StandardCharsets.UTF_8)
}

fun decodeUnicode(input: String): String {
    val regex = "\\u([0-9a-fA-F]{4})".toRegex()
    var result = input
    regex.findAll(input).forEach {
        val unicode = it.groupValues[1]
        val char = Character.toChars(unicode.toInt(16))[0]
        result = result.replace(it.value, char.toString())
    }
    return result
}

/**
 * @return 检查字符串是否为null，如果是那么则返回""，如果不是，则返回字符串本身
 */
fun getStringNotNull(string: String?): String = string ?: ""

/**
 * [Modified from PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher/blob/84aca2e/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/Tools.java#L1032-L1039)
 */
fun String.extractUntilCharacter(whatFor: String, terminator: Char): String? {
    var whatForStart = indexOf(whatFor)
    if (whatForStart == -1) return null
    whatForStart += whatFor.length
    val terminatorIndex = indexOf(terminator, whatForStart)
    if (terminatorIndex == -1) return null
    return substring(whatForStart, terminatorIndex)
}

/**
 * 获取字符串指定行的内容
 */
fun String.getLine(line: Int): String? {
    val lines = this.trimIndent().split("\n")
    return if (line in 1..lines.size) lines[line - 1] else null
}

fun insertJSONValueList(args: Array<String>, keyValueMap: Map<String, String>) =
    args.map {
        keyValueMap.entries.fold(it) {
            acc, (k, v) ->
            acc.replace("\${'$'}{k}", v)
        }
    }.toTypedArray()

fun String.splitPreservingQuotes(delimiter: Char = ' ') : List<String> {
    val result = mutableListOf<String>()
    val currentPart = StringBuilder()
    var inQuotes = false

    for ((index, c) in withIndex()) {
        when {
            c == '"' && (index == 0 || this[index - 1] != '\\') -> {
                // 切换引号状态（忽略转义引号）
                inQuotes = !inQuotes
            }
            c == delimiter && !inQuotes -> {
                // 如果不在引号内且遇到空格，则结束当前部分并添加到结果中
                if (currentPart.isNotEmpty()) {
                    result.add(currentPart.toString())
                    currentPart.clear() // 清空当前部分
                }
            }
            else -> {
                // 将字符添加到当前部分
                currentPart.append(c)
            }
        }
    }

    // 添加最后一部分（如果有的话）
    if (currentPart.isNotEmpty()) {
        result.add(currentPart.toString())
    }

    return result
}

fun String.isSurrounded(prefix: String, suffix: String): Boolean = this.startsWith(prefix) && this.endsWith(suffix)

fun String.toFullUnicode(): String {
    return this.map { "\\u%04x".format(it.code) }.joinToString("")
}

fun String.toUnicodeEscaped(): String {
    return this.flatMap { ch ->
        if (ch.code > 127) {
            val hex = ch.code.toString(16).padStart(4, '0')
            listOf("\\u$hex")
        } else {
            listOf(ch.toString())
        }
    }.joinToString("")
}

/**
 * 过滤掉颜色占位符
 */
fun String.stripColorCodes(): String {
    return replace(Regex("§[0-9a-fk-orA-FK-OR]"), "")
}

fun String.isEmptyOrBlank(): Boolean = this.isEmpty() || this.isBlank()

fun String.isNotEmptyOrBlank(): Boolean = !this.isEmptyOrBlank()

/**
 * 检查一段字符串内是否含有中文字符（中文标点）
 * @return 是否带有中文
 */
fun String?.containsChinese(): Boolean {
    if (this == null || this.isEmpty()) {
        return false
    }

    val pattern = Pattern.compile("[一-龥|！，。（）《》“”？：；【】]")
    val matcher = pattern.matcher(this)
    return matcher.find()
}