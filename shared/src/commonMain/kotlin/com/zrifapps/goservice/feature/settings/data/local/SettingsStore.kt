package com.zrifapps.goservice.feature.settings.data.local

/**
 * Lightweight key-value store for app preferences (SharedPreferences on Android,
 * NSUserDefaults on iOS). Kept separate from the Room domain database on purpose:
 * preferences are not domain data and should not trigger schema migrations.
 */
expect class SettingsStore {
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
}
