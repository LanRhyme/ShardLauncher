package com.lanrhyme.shardlauncher.game.path

import java.io.File

private fun String.replaceSeparator(): String = this.replace("/", File.separator)

fun getGameHome(): String = GamePathManager.currentPath

fun getVersionsHome(): String = "${getGameHome()}/versions".replaceSeparator()

fun getLibrariesHome(): String = "${getGameHome()}/libraries".replaceSeparator()

fun getAssetsHome(): String = "${getGameHome()}/assets".replaceSeparator()

fun getResourcesHome(): String = "${getGameHome()}/resources".replaceSeparator()