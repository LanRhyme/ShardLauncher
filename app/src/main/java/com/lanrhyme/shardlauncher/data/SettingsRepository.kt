package com.lanrhyme.shardlauncher.data

import android.content.Context
import com.lanrhyme.shardlauncher.common.SidebarPosition
import com.lanrhyme.shardlauncher.ui.theme.ThemeColor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

class SettingsRepository(context: Context) {

    private val properties = Properties()
    private val settingsFile: File

    init {
        val dataDir = context.getExternalFilesDir(null)
        settingsFile = File(dataDir, PREFS_NAME)
        if (settingsFile.exists()) {
            FileInputStream(settingsFile).use { properties.load(it) }
        }
    }

    private fun saveProperties() {
        FileOutputStream(settingsFile).use { properties.store(it, null) }
    }

    fun getUiScale(): Float {
        return properties.getProperty(KEY_UI_SCALE, "1.0").toFloat()
    }

    fun setUiScale(scale: Float) {
        properties.setProperty(KEY_UI_SCALE, scale.toString())
        saveProperties()
    }

    fun getEnableVersionCheck(): Boolean {
        return properties.getProperty(KEY_ENABLE_VERSION_CHECK, "true").toBoolean()
    }

    fun setEnableVersionCheck(enabled: Boolean) {
        properties.setProperty(KEY_ENABLE_VERSION_CHECK, enabled.toString())
        saveProperties()
    }

    fun getLauncherBackgroundUri(): String? {
        return properties.getProperty(KEY_LAUNCHER_BACKGROUND_URI)
    }

    fun setLauncherBackgroundUri(uri: String?) {
        if (uri != null) {
            properties.setProperty(KEY_LAUNCHER_BACKGROUND_URI, uri)
        } else {
            properties.remove(KEY_LAUNCHER_BACKGROUND_URI)
        }
        saveProperties()
    }

    fun getLauncherBackgroundBlur(): Float {
        return properties.getProperty(KEY_LAUNCHER_BACKGROUND_BLUR, "0.0").toFloat()
    }

    fun setLauncherBackgroundBlur(blur: Float) {
        properties.setProperty(KEY_LAUNCHER_BACKGROUND_BLUR, blur.toString())
        saveProperties()
    }

    fun getLauncherBackgroundBrightness(): Float {
        return properties.getProperty(KEY_LAUNCHER_BACKGROUND_BRIGHTNESS, "0.0").toFloat()
    }

    fun setLauncherBackgroundBrightness(brightness: Float) {
        properties.setProperty(KEY_LAUNCHER_BACKGROUND_BRIGHTNESS, brightness.toString())
        saveProperties()
    }

    fun getEnableBackgroundLightEffect(): Boolean {
        return properties.getProperty(KEY_ENABLE_BACKGROUND_LIGHT_EFFECT, "true").toBoolean()
    }

    fun setEnableBackgroundLightEffect(enabled: Boolean) {
        properties.setProperty(KEY_ENABLE_BACKGROUND_LIGHT_EFFECT, enabled.toString())
        saveProperties()
    }

    fun getAnimationSpeed(): Float {
        return properties.getProperty(KEY_ANIMATION_SPEED, "1.0f").toFloat()
    }

    fun setAnimationSpeed(speed: Float) {
        properties.setProperty(KEY_ANIMATION_SPEED, speed.toString())
        saveProperties()
    }

    fun getLightEffectAnimationSpeed(): Float {
        return properties.getProperty(KEY_LIGHT_EFFECT_ANIMATION_SPEED, "1.0f").toFloat()
    }

    fun setLightEffectAnimationSpeed(speed: Float) {
        properties.setProperty(KEY_LIGHT_EFFECT_ANIMATION_SPEED, speed.toString())
        saveProperties()
    }

    fun getIsDarkTheme(systemIsDark: Boolean): Boolean {
        return properties.getProperty(KEY_IS_DARK_THEME, systemIsDark.toString()).toBoolean()
    }

    fun setIsDarkTheme(isDark: Boolean) {
        properties.setProperty(KEY_IS_DARK_THEME, isDark.toString())
        saveProperties()
    }

    fun getSidebarPosition(): SidebarPosition {
        val positionName = properties.getProperty(KEY_SIDEBAR_POSITION, SidebarPosition.Left.name)
        return SidebarPosition.valueOf(positionName ?: SidebarPosition.Left.name)
    }

    fun setSidebarPosition(position: SidebarPosition) {
        properties.setProperty(KEY_SIDEBAR_POSITION, position.name)
        saveProperties()
    }

    fun getThemeColor(): ThemeColor {
        val colorName = properties.getProperty(KEY_THEME_COLOR, ThemeColor.Green.name)
        return ThemeColor.valueOf(colorName ?: ThemeColor.Green.name)
    }

    fun setThemeColor(color: ThemeColor) {
        properties.setProperty(KEY_THEME_COLOR, color.name)
        saveProperties()
    }

    companion object {
        private const val PREFS_NAME = "developer_settings.properties"
        private const val KEY_UI_SCALE = "ui_scale"
        private const val KEY_ENABLE_VERSION_CHECK = "enable_version_check"
        private const val KEY_LAUNCHER_BACKGROUND_URI = "launcher_background_uri"
        private const val KEY_LAUNCHER_BACKGROUND_BLUR = "launcher_background_blur"
        private const val KEY_LAUNCHER_BACKGROUND_BRIGHTNESS = "launcher_background_brightness"
        private const val KEY_ENABLE_BACKGROUND_LIGHT_EFFECT = "enable_background_light_effect"
        private const val KEY_ANIMATION_SPEED = "animation_speed"
        private const val KEY_LIGHT_EFFECT_ANIMATION_SPEED = "light_effect_animation_speed"
        private const val KEY_IS_DARK_THEME = "is_dark_theme"
        private const val KEY_SIDEBAR_POSITION = "sidebar_position"
        private const val KEY_THEME_COLOR = "theme_color"
    }
}