package com.lanrhyme.shardlauncher.game.versioninfo.models

import com.google.gson.annotations.SerializedName

enum class OperatingSystem(val identifier: String) {
    /**
     * Microsoft Windows.
     */
    @SerializedName("windows")
    Windows("windows"),

    /**
     * Linux and Unix like OS, including Solaris.
     * (Android here)
     */
    @SerializedName("linux")
    Linux("linux"),

    /**
     * Mac OS X.
     */
    @SerializedName("osx")
    MacOS("osx"),

    /**
     * Unknown operating system.
     */
    @SerializedName("universal")
    Unknown("universal")
}