package com.zrifapps.goservice.feature.settings.data.local

import platform.Foundation.NSUserDefaults

actual class SettingsStore {

    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun getBoolean(key: String, default: Boolean): Boolean =
        if (defaults.objectForKey(key) == null) default else defaults.boolForKey(key)

    actual fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }
}
