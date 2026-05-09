package com.zrifapps.goservice.ads

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.compose.InterstitialAdLoaderState
import com.yandex.mobile.ads.compose.rememberInterstitialAdLoader
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class YandexInterstitialController internal constructor(
    private val loader: InterstitialAdLoaderState,
    private val scope: CoroutineScope,
    private val adUnitId: String,
) {
    private var ad: InterstitialAd? = null
    var isReady: Boolean = false
        private set

    fun load() {
        if (isReady) return
        scope.launch {
            when (val r = loader.loadAd(AdRequest.Builder(adUnitId).build())) {
                is InterstitialAdLoadResult.Success -> {
                    ad = r.ad
                    isReady = true
                }
                is InterstitialAdLoadResult.Failure -> {
                    isReady = false
                }
            }
        }
    }

    fun show(activity: Activity) {
        val current = ad ?: return
        current.setAdEventListener(object : InterstitialAdEventListener {
            override fun onAdShown() = Unit
            override fun onAdFailedToShow(adError: AdError) {
                clearAndReload()
            }
            override fun onAdDismissed() {
                clearAndReload()
            }
            override fun onAdClicked() = Unit
            override fun onAdImpression(impressionData: ImpressionData?) = Unit
        })
        current.show(activity)
    }

    private fun clearAndReload() {
        ad?.setAdEventListener(null)
        ad = null
        isReady = false
        load()
    }
}

@Composable
fun rememberYandexInterstitial(
    adUnitId: String = AdsConfig.INTERSTITIAL_UNIT_ID,
): YandexInterstitialController {
    val loaderState = rememberInterstitialAdLoader()
    val scope = rememberCoroutineScope()
    val controller = remember(loaderState, adUnitId) {
        YandexInterstitialController(loaderState, scope, adUnitId)
    }
    LaunchedEffect(controller) { controller.load() }
    return controller
}
