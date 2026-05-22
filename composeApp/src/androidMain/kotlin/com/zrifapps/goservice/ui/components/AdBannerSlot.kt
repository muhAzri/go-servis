package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zrifapps.goservice.ads.YandexBannerInline

@Composable
fun AdBannerSlot(
    modifier: Modifier = Modifier,
    maxHeightDp: Int = 100,
) {
    // YandexBannerInline keeps a zero-height, alpha-0 footprint until fill, then
    // fades + expands into place. On no-fill / failure it stays invisible.
    YandexBannerInline(
        maxHeightDp = maxHeightDp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    )
}
