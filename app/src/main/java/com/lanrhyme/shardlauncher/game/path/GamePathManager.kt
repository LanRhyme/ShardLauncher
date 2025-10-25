package com.lanrhyme.shardlauncher.game.path

import com.lanrhyme.shardlauncher.path.PathManager
import java.io.File

object GamePathManager {
    val currentPath: String = File(PathManager.DIR_FILES_EXTERNAL, ".minecraft").absolutePath
}