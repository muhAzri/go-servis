package com.zrifapps.goservice.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

class AppOpenAdManager(private val application: Application) : AppOpenAdLoadListener {

    @Volatile var canShowNow: Boolean = false

    private val loader = AppOpenAdLoader(application)
    private val request = AdRequest.Builder(AdsConfig.APP_OPEN_UNIT_ID).build()
    private val loading = AtomicBoolean(false)

    private var ad: AppOpenAd? = null
    private var loadedAt: Long = 0L
    private var lastShownAt: Long = 0L
    private var activityRef: WeakReference<Activity>? = null
    private var hasBeenInBackground = false

    private val processObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            if (hasBeenInBackground) showIfReady()
        }
        override fun onStop(owner: LifecycleOwner) {
            hasBeenInBackground = true
        }
    }

    private val activityObserver = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(a: Activity, b: Bundle?) = Unit
        override fun onActivityStarted(a: Activity) {
            activityRef = WeakReference(a)
        }
        override fun onActivityResumed(a: Activity) = Unit
        override fun onActivityPaused(a: Activity) = Unit
        override fun onActivityStopped(a: Activity) = Unit
        override fun onActivitySaveInstanceState(a: Activity, o: Bundle) = Unit
        override fun onActivityDestroyed(a: Activity) = Unit
    }

    fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(processObserver)
        application.registerActivityLifecycleCallbacks(activityObserver)
        loadAd()
    }

    private fun loadAd() {
        if (loading.compareAndSet(false, true)) loader.loadAd(request, this)
    }

    override fun onAdLoaded(appOpenAd: AppOpenAd) {
        ad = appOpenAd
        loadedAt = System.currentTimeMillis()
        loading.set(false)
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        loading.set(false)
    }

    private fun showIfReady() {
        val activity = activityRef?.get() ?: return
        if (!canShowNow) return
        if (System.currentTimeMillis() - lastShownAt < SHOW_COOLDOWN_MS) return
        val cached = ad ?: run { loadAd(); return }
        if (System.currentTimeMillis() - loadedAt > FRESHNESS_MS) {
            clearAd()
            loadAd()
            return
        }
        cached.setAdEventListener(eventListener)
        cached.show(activity)
    }

    private fun clearAd() {
        ad?.setAdEventListener(null)
        ad = null
    }

    private val eventListener = object : AppOpenAdEventListener {
        override fun onAdShown() {
            lastShownAt = System.currentTimeMillis()
        }
        override fun onAdFailedToShow(adError: AdError) {
            clearAd()
            loadAd()
        }
        override fun onAdDismissed() {
            clearAd()
            loadAd()
        }
        override fun onAdClicked() = Unit
        override fun onAdImpression(impressionData: ImpressionData?) = Unit
    }

    companion object {
        private const val FRESHNESS_MS = 4L * 60L * 60L * 1000L
        private const val SHOW_COOLDOWN_MS = 60L * 1000L
    }
}
