package com.zrifapps.goservice.ads

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.common.AdBindingResult
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.nativeads.MediaView
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdEventListener
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdOptions
import com.yandex.mobile.ads.nativeads.NativeAdView
import com.yandex.mobile.ads.nativeads.NativeAdViewBinder
import com.zrifapps.goservice.R

@Composable
fun YandexNativeAd(
    adUnitId: String = AdsConfig.NATIVE_CONTENT_UNIT_ID,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val loader = remember(adUnitId) { NativeAdLoader(context) }
    var loadedAd by remember { mutableStateOf<NativeAd?>(null) }

    LaunchedEffect(adUnitId) {
        val options = NativeAdOptions.Builder()
            .setShouldLoadImagesAutomatically(true)
            .build()
        loader.loadAd(
            AdRequest.Builder(adUnitId).build(),
            options,
            object : NativeAdLoadListener {
                override fun onAdLoaded(nativeAd: NativeAd) {
                    loadedAd = nativeAd
                }
                override fun onAdFailedToLoad(error: AdRequestError) = Unit
            },
        )
    }

    DisposableEffect(loader) {
        onDispose {
            loader.cancelLoading()
            loadedAd?.setNativeAdEventListener(null)
            loadedAd = null
        }
    }

    val ad = loadedAd ?: return
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            val view = LayoutInflater.from(ctx)
                .inflate(R.layout.yandex_native_ad, null, false) as NativeAdView
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            view
        },
        update = { rootView ->
            val binder = NativeAdViewBinder.Builder(rootView)
                .setTitleView(rootView.findViewById<TextView>(R.id.yandex_native_title))
                .setBodyView(rootView.findViewById<TextView>(R.id.yandex_native_body))
                .setCallToActionView(rootView.findViewById<TextView>(R.id.yandex_native_call_to_action))
                .setDomainView(rootView.findViewById<TextView>(R.id.yandex_native_domain))
                .setSponsoredView(rootView.findViewById<TextView>(R.id.yandex_native_sponsored))
                .setWarningView(rootView.findViewById<TextView>(R.id.yandex_native_warning))
                .setIconView(rootView.findViewById<ImageView>(R.id.yandex_native_icon))
                .setFeedbackView(rootView.findViewById<ImageView>(R.id.yandex_native_feedback))
                .setMediaView(rootView.findViewById<MediaView>(R.id.yandex_native_media))
                .build()

            when (ad.bindNativeAd(binder)) {
                is AdBindingResult.Success -> {
                    ad.setNativeAdEventListener(object : NativeAdEventListener {
                        override fun onAdClicked() = Unit
                        override fun onImpression(impressionData: ImpressionData?) = Unit
                    })
                }
                is AdBindingResult.Failure -> Unit
            }
        },
    )
}
