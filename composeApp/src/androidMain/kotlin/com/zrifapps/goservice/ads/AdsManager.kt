package com.zrifapps.goservice.ads

import android.app.Application
import com.yandex.mobile.ads.common.YandexAds

object AdsManager {
    @Volatile private var initialized = false
    private lateinit var appOpen: AppOpenAdManager

    fun initialize(application: Application) {
        if (initialized) return
        initialized = true

        YandexAds.setUserConsent(true)
        YandexAds.setLocationTracking(false)
        YandexAds.setAgeRestricted(false)
        YandexAds.enableLogging(true)

        YandexAds.initialize(application) {
            appOpen = AppOpenAdManager(application).also { it.start() }
        }
    }

    fun setAppOpenAllowed(allowed: Boolean) {
        if (::appOpen.isInitialized) appOpen.canShowNow = allowed
    }
}
