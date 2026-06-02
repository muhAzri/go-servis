package com.zrifapps.goservice.ads

import android.app.Activity
import android.app.Application
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Shows a Yandex interstitial at task-completion points (service saved, reminder
 * completed/added, vehicle added). All call sites share one frequency policy:
 * the user's first qualifying action is ad-free, then at most one ad per
 * [SHOW_COOLDOWN_MS]. If no ad is ready the flow proceeds immediately — the user
 * is never blocked waiting for fill.
 */
class InterstitialAdManager(application: Application) : InterstitialAdLoadListener {

    private val loader = InterstitialAdLoader(application)
    private val request = AdRequest.Builder(AdsConfig.INTERSTITIAL_UNIT_ID).build()
    private val loading = AtomicBoolean(false)

    private var ad: InterstitialAd? = null
    private var loadedAt: Long = 0L
    private var lastShownAt: Long = 0L
    private var qualifyingActions: Int = 0
    private var pendingOnFinished: (() -> Unit)? = null

    fun start() {
        loadAd()
    }

    private fun loadAd() {
        if (loading.compareAndSet(false, true)) loader.loadAd(request, this)
    }

    override fun onAdLoaded(interstitialAd: InterstitialAd) {
        ad = interstitialAd
        loadedAt = System.currentTimeMillis()
        loading.set(false)
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        loading.set(false)
    }

    /**
     * Run [onFinished] after an interstitial is dismissed, or immediately when the
     * frequency policy skips this one or no ad is ready. [onFinished] always runs
     * exactly once so the caller's navigation never stalls.
     */
    fun maybeShow(activity: Activity, onFinished: () -> Unit) {
        qualifyingActions++

        val now = System.currentTimeMillis()
        val isFirstAction = qualifyingActions <= SKIP_FIRST_ACTIONS
        val withinCooldown = now - lastShownAt < SHOW_COOLDOWN_MS
        val cached = ad
        val stale = cached != null && now - loadedAt > FRESHNESS_MS

        if (isFirstAction || withinCooldown || cached == null || stale) {
            if (cached == null || stale) {
                clearAd()
                loadAd() // keep one warm for next time
            }
            onFinished()
            return
        }

        pendingOnFinished = onFinished
        cached.setAdEventListener(eventListener)
        cached.show(activity)
    }

    private fun clearAd() {
        ad?.setAdEventListener(null)
        ad = null
    }

    private fun finishOnce() {
        val callback = pendingOnFinished
        pendingOnFinished = null
        callback?.invoke()
    }

    private val eventListener = object : InterstitialAdEventListener {
        override fun onAdShown() {
            lastShownAt = System.currentTimeMillis()
        }
        override fun onAdFailedToShow(adError: AdError) {
            clearAd()
            loadAd()
            finishOnce()
        }
        override fun onAdDismissed() {
            clearAd()
            loadAd()
            finishOnce()
        }
        override fun onAdClicked() = Unit
        override fun onAdImpression(impressionData: ImpressionData?) = Unit
    }

    companion object {
        private const val FRESHNESS_MS = 4L * 60L * 60L * 1000L
        private const val SHOW_COOLDOWN_MS = 3L * 60L * 1000L // 3 minutes between ads
        private const val SKIP_FIRST_ACTIONS = 1 // first qualifying action is ad-free
    }
}
