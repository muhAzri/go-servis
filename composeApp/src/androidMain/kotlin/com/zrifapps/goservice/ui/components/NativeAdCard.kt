package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zrifapps.goservice.ads.YandexNativeAd

@Composable
fun NativeAdCard(modifier: Modifier = Modifier) {
    // YandexNativeAd renders zero-height while loading and fades in once the
    // ad fills. If no fill, nothing is ever drawn.
    YandexNativeAd(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    )
}
