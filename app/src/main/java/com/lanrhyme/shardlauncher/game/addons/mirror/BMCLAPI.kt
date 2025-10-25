package com.lanrhyme.shardlauncher.game.addons.mirror

import com.lanrhyme.shardlauncher.setting.enums.MirrorSourceType

private const val ROOT = "https://bmclapi2.bangbang93.com"

enum class BMCLAPI(val url: String) {
    BASE_URL(ROOT),
    MAVEN("$ROOT/maven"),
    ASSETS("$ROOT/assets"),
    LIBRARIES("$ROOT/libraries")
}

/**
 * [Modified from HMCL](https://github.com/HMCL-dev/HMCL/blob/9aa1367/HMCLCore/src/main/java/org/jackhuang/hmcl/download/BMCLAPIDownloadProvider.java#L64-L83)
 */
private val REPLACE_MIRROR_HOLDERS = mapOf(
    Pair(BMCLAPI.BASE_URL.url, BMCLAPI.BASE_URL.url),
    Pair("https://launchermeta.mojang.com", BMCLAPI.BASE_URL.url),
    Pair("https://piston-meta.mojang.com", BMCLAPI.BASE_URL.url),
    Pair("https://piston-data.mojang.com", BMCLAPI.BASE_URL.url),
    Pair("https://launcher.mojang.com", BMCLAPI.BASE_URL.url),
    Pair("https://libraries.minecraft.net", BMCLAPI.LIBRARIES.url),
    Pair("https://resources.download.minecraft.net", BMCLAPI.ASSETS.url),
    Pair("http://files.minecraftforge.net/maven", BMCLAPI.MAVEN.url),
    Pair("https://files.minecraftforge.net/maven", BMCLAPI.MAVEN.url),
    Pair("https://maven.minecraftforge.net", BMCLAPI.MAVEN.url),
    Pair("https://maven.neoforged.net/releases/net/neoforged/forge", BMCLAPI.MAVEN.url + "/net/neoforged/forge"),
    Pair("https://maven.neoforged.net/releases/net/neoforged/neoforge", BMCLAPI.MAVEN.url + "/net/neoforged/neoforge"),
    Pair("http://dl.liteloader.com/versions/versions.json", BMCLAPI.MAVEN.url + "/com/mumfrey/liteloader/versions.json"),
    Pair("http://dl.liteloader.com/versions", BMCLAPI.MAVEN.url),
    Pair("https://meta.fabricmc.net", BMCLAPI.BASE_URL.url + "/fabric-meta"),
    Pair("https://maven.fabricmc.net", BMCLAPI.MAVEN.url),
    Pair("https://authlib-injector.yushi.moe", BMCLAPI.BASE_URL.url + "/mirrors/authlib-injector"),
    Pair("https://repo1.maven.org/maven2", "https://mirrors.cloud.tencent.com/nexus/repository/maven-public")
)

/**
 * 替换为 BMCL API 镜像源链接，若如匹配的链接，则仅返回官方链接集合
 */
fun String.mapMirrorableUrls(fileDownloadSource: MirrorSourceType = MirrorSourceType.OFFICIAL_FIRST): List<String> {
    var isAssetsFile = false

    val mirrorUrl = REPLACE_MIRROR_HOLDERS.entries.find { (key, mirror) ->
        isAssetsFile = mirror == BMCLAPI.ASSETS.url
        this.startsWith(key)
    }?.let { (origin, mirror) ->
        this.replaceFirst(origin, mirror)
    }

    val type = if (!isAssetsFile) {
        fileDownloadSource
    } else {
        //资源文件数量过多，请求量大，应先尝试官方源，减轻 BMCL API 源压力
        MirrorSourceType.OFFICIAL_FIRST
    }

    return when (type) {
        MirrorSourceType.OFFICIAL_FIRST -> listOfNotNull(this, mirrorUrl)
        MirrorSourceType.MIRROR_FIRST -> listOfNotNull(mirrorUrl, this)
    }
}