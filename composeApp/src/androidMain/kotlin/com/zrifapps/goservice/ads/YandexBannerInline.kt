package com.zrifapps.goservice.ads

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.compose.Banner
import com.yandex.mobile.ads.compose.BannerEvents
import com.yandex.mobile.ads.compose.BannerSize
import com.yandex.mobile.ads.compose.rememberBannerAdState

@Composable
fun YandexBannerInline(
    adUnitId: String = AdsConfig.BANNER_UNIT_ID,
    maxHeightDp: Int = 250,
    onLoadStateChange: (AdLoadState) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val widthDp = maxWidth
        if (widthDp < 50.dp) return@BoxWithConstraints

        var loaded by remember { mutableStateOf(false) }
        val state = rememberBannerAdState(
            adSize = BannerSize.Inline(width = widthDp, maxHeight = maxHeightDp.dp),
            events = BannerEvents(
                onAdLoaded = {
                    loaded = true
                    onLoadStateChange(AdLoadState.Loaded)
                },
                onAdFailedToLoad = { onLoadStateChange(AdLoadState.Failed) },
            ),
        )
        LaunchedEffect(adUnitId, widthDp) {
            state.loadAd(AdRequest.Builder(adUnitId).build())
        }
        val alpha by animateFloatAsState(
            targetValue = if (loaded) 1f else 0f,
            animationSpec = tween(durationMillis = 220),
            label = "yandex-banner-alpha",
        )
        Banner(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = if (loaded) Dp.Unspecified else 0.dp)
                .alpha(alpha),
        )
    }
}
