package com.lanrhyme.shardlauncher.model.version

import com.google.gson.annotations.SerializedName

data class GameManifest(
    val arguments: Arguments?,
    val assetIndex: AssetIndexInfo?,
    val assets: String?,
    val complianceLevel: Int?,
    val downloads: Downloads?,
    val id: String,
    val javaVersion: JavaVersion?,
    val libraries: List<Library>?,
    val mainClass: String?,
    val minecraftArguments: String?,
    val minimumLauncherVersion: Int?,
    val releaseTime: String?,
    val time: String?,
    val type: String?,
    val logging: Logging?,
    val inheritsFrom: String?
)

data class Arguments(
    val game: List<Any>?,
    val jvm: List<Any>?
)

data class AssetIndexInfo(
    val id: String?,
    val sha1: String?,
    val size: Long?,
    val totalSize: Long?,
    val url: String?
)

data class Downloads(
    val client: Client?,
    @SerializedName("client_mappings") val clientMappings: ClientMappings?,
    val server: Server?,
    @SerializedName("server_mappings") val serverMappings: ServerMappings?
)

data class Client(
    val sha1: String?,
    val size: Long?,
    val url: String?
)

data class ClientMappings(
    val sha1: String?,
    val size: Long?,
    val url: String?
)

data class Server(
    val sha1: String?,
    val size: Long?,
    val url: String?
)

data class ServerMappings(
    val sha1: String?,
    val size: Long?,
    val url: String?
)

data class JavaVersion(
    val component: String?,
    val majorVersion: Int?
)

data class Library(
    val downloads: LibraryDownloads?,
    val name: String,
    val natives: Map<String, String>?,
    val rules: List<Rule>?
) {
    fun isNative(): Boolean = natives != null && Rule.checkRules(rules)
}

data class LibraryDownloads(
    val artifact: Artifact?,
    val classifiers: Map<String, Artifact>?
)

data class Artifact(
    val path: String?,
    val sha1: String?,
    val size: Long?,
    val url: String?
)

data class Rule(
    val action: String, // "allow" or "disallow"
    val os: Os?
) {
    companion object {
        fun checkRules(rules: List<Rule>?): Boolean {
            if (rules.isNullOrEmpty()) return true // always allow
            // This is a simplified check. A full implementation would need to match the current OS.
            // For now, we'll follow the simple "disallow osx" logic from your reference.
            for (rule in rules) {
                if (rule.action == "allow" && rule.os?.name == "osx") {
                    return false // disallow
                }
            }
            return true // allow if none match
        }
    }
}

data class Os(
    val name: String? // "osx", "linux", "windows"
)

data class Logging(
    val client: LoggingClient?
)

data class LoggingClient(
    val argument: String?,
    val file: LoggingFile?,
    val type: String?
)

data class LoggingFile(
    val id: String?,
    val sha1: String?,
    val size: Long?,
    val url: String?
)
