package com.zrifapps.goservice.ads

import android.app.Activity
import android.app.Application
import com.yandex.mobile.ads.common.YandexAds

object AdsManager {
    @Volatile private var initialized = false
    private lateinit var appOpen: AppOpenAdManager
    private lateinit var interstitial: InterstitialAdManager

    fun initialize(application: Application) {
        if (initialized) return
        initialized = true

        YandexAds.setUserConsent(true)
        YandexAds.setLocationTracking(false)
        YandexAds.setAgeRestricted(false)
        YandexAds.enableLogging(true)

        YandexAds.initialize(application) {
            appOpen = AppOpenAdManager(application).also { it.start() }
            interstitial = InterstitialAdManager(application).also { it.start() }
        }
    }

    fun setAppOpenAllowed(allowed: Boolean) {
        if (::appOpen.isInitialized) appOpen.canShowNow = allowed
    }

    /**
     * Show an interstitial at a task-completion point, then run [onFinished].
     * Falls through to [onFinished] immediately if ads aren't ready yet or the
     * Activity is gone, so navigation never blocks on an ad.
     */
    fun showInterstitial(activity: Activity?, onFinished: () -> Unit) {
        if (activity != null && ::interstitial.isInitialized) {
            interstitial.maybeShow(activity, onFinished)
        } else {
            onFinished()
        }
    }
}
