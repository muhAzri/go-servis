package com.zrifapps.goservice.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.zrifapps.goservice.ui.components.BottomNavBar
import com.zrifapps.goservice.ui.components.BottomTab
import com.zrifapps.goservice.ui.main.tabs.GarasiTab
import com.zrifapps.goservice.ui.main.tabs.PengaturanTab
import com.zrifapps.goservice.ui.main.tabs.PengingatTab
import com.zrifapps.goservice.ui.main.tabs.RiwayatTab
import com.zrifapps.goservice.ui.theme.AppColors

@Composable
fun MainTabsScreen(
    onAddService: () -> Unit = {},
    onOpenTestScreen: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(BottomTab.Beranda) }

    Column(
        modifier = Modifier
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            when (selectedTab) {
                BottomTab.Beranda -> GarasiTab()
                BottomTab.Pengingat -> PengingatTab()
                BottomTab.Riwayat -> RiwayatTab()
                BottomTab.Saya -> PengaturanTab(onOpenTestScreen = onOpenTestScreen)
                BottomTab.Add -> GarasiTab()
            }
        }
        BottomNavBar(
            selected = selectedTab,
            onSelect = { tab ->
                if (tab == BottomTab.Add) onAddService() else selectedTab = tab
            },
        )
    }
}
