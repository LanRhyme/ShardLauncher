package com.lanrhyme.shardlauncher.data

import android.content.Context
import android.content.SharedPreferences
import com.lanrhyme.shardlauncher.common.SidebarPosition
import com.lanrhyme.shardlauncher.ui.theme.ThemeColor

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAnimationSpeed(): Float {
        return prefs.getFloat(KEY_ANIMATION_SPEED, 1.0f)
    }

    fun setAnimationSpeed(speed: Float) {
        prefs.edit().putFloat(KEY_ANIMATION_SPEED, speed).apply()
    }

    fun getIsDarkTheme(systemIsDark: Boolean): Boolean {
        return prefs.getBoolean(KEY_IS_DARK_THEME, systemIsDark)
    }

    fun setIsDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK_THEME, isDark).apply()
    }

    fun getSidebarPosition(): SidebarPosition {
        val positionName = prefs.getString(KEY_SIDEBAR_POSITION, SidebarPosition.Left.name)
        return SidebarPosition.valueOf(positionName ?: SidebarPosition.Left.name)
    }

    fun setSidebarPosition(position: SidebarPosition) {
        prefs.edit().putString(KEY_SIDEBAR_POSITION, position.name).apply()
    }

    fun getThemeColor(): ThemeColor {
        val colorName = prefs.getString(KEY_THEME_COLOR, ThemeColor.Green.name)
        return ThemeColor.valueOf(colorName ?: ThemeColor.Green.name)
    }

    fun setThemeColor(color: ThemeColor) {
        prefs.edit().putString(KEY_THEME_COLOR, color.name).apply()
    }

    companion object {
        private const val PREFS_NAME = "developer_settings"
        private const val KEY_ANIMATION_SPEED = "animation_speed"
        private const val KEY_IS_DARK_THEME = "is_dark_theme"
        private const val KEY_SIDEBAR_POSITION = "sidebar_position"
        private const val KEY_THEME_COLOR = "theme_color"
    }
}
