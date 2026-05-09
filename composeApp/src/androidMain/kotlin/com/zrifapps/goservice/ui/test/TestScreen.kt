package com.zrifapps.goservice.ui.test

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zrifapps.goservice.Greeting
import com.zrifapps.goservice.ads.YandexBannerInline
import com.zrifapps.goservice.ads.YandexNativeAd
import com.zrifapps.goservice.ads.findActivity
import com.zrifapps.goservice.ads.rememberYandexInterstitial
import com.zrifapps.goservice.ui.theme.AppColors
import goservice.composeapp.generated.resources.Res
import goservice.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun TestScreen(
    onBack: () -> Unit = {},
    onAddVehicle: () -> Unit = {},
) {
    var showContent by remember { mutableStateOf(false) }
    var showBanner by remember { mutableStateOf(false) }
    var showNative by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val interstitial = rememberYandexInterstitial()

    Column(
        modifier = Modifier
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Test Screen",
                style = MaterialTheme.typography.titleLarge,
                color = AppColors.TextPrimary,
            )
            Button(onClick = onBack) {
                Text("← Kembali")
            }
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            Button(onClick = onAddVehicle) {
                Text("Tambah Kendaraan")
            }
            Spacer(Modifier.height(8.dp))
            Text("— Yandex Ads test —", style = MaterialTheme.typography.titleSmall)
            Button(onClick = { showBanner = !showBanner }) {
                Text(if (showBanner) "Hide inline banner" else "Test inline banner")
            }
            Button(
                enabled = interstitial.isReady,
                onClick = { context.findActivity()?.let(interstitial::show) },
            ) {
                Text(if (interstitial.isReady) "Test interstitial" else "Loading interstitial…")
            }
            Button(onClick = { showNative = !showNative }) {
                Text(if (showNative) "Hide native ad" else "Test native ad")
            }

            if (showBanner) {
                YandexBannerInline(modifier = Modifier.fillMaxWidth())
            }
            if (showNative) {
                YandexNativeAd(modifier = Modifier.fillMaxWidth())
            }

            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}

@Preview
@Composable
private fun TestScreenPreview() {
    TestScreen()
}
