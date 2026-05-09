package com.zrifapps.goservice.ads

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val widthDp = maxWidth
        if (widthDp < 50.dp) return@BoxWithConstraints

        val state = rememberBannerAdState(
            adSize = BannerSize.Inline(width = widthDp, maxHeight = maxHeightDp.dp),
            events = BannerEvents(),
        )
        LaunchedEffect(adUnitId, widthDp) {
            state.loadAd(AdRequest.Builder(adUnitId).build())
        }
        Banner(state = state, modifier = Modifier.fillMaxWidth())
    }
}
