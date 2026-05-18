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
import com.zrifapps.goservice.ui.main.tabs.HistoryTab
import com.zrifapps.goservice.ui.main.tabs.HomeTab
import com.zrifapps.goservice.ui.main.tabs.RemindersTab
import com.zrifapps.goservice.ui.main.tabs.SettingsTab
import com.zrifapps.goservice.ui.theme.AppColors

@Composable
fun MainTabsScreen(
    onAddService: () -> Unit = {},
    onAddVehicle: () -> Unit = {},
    onUpdateOdometer: () -> Unit = {},
    onOpenTips: () -> Unit = {},
    onOpenReminderDetail: () -> Unit = {},
    onOpenVehicleDetail: () -> Unit = {},
    onOpenTestScreen: () -> Unit = {},
    onOpenPrivacy: () -> Unit = {},
    onOpenTerms: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }

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
                BottomTab.Home -> HomeTab(
                    onOpenReminders = { selectedTab = BottomTab.Reminders },
                    onOpenReminderDetail = onOpenReminderDetail,
                    onOpenVehicleDetail = onOpenVehicleDetail,
                    onAddService = onAddService,
                    onAddVehicle = onAddVehicle,
                    onUpdateOdometer = onUpdateOdometer,
                    onOpenTips = onOpenTips,
                )
                BottomTab.Reminders -> RemindersTab(
                    onOpenReminderDetail = onOpenReminderDetail,
                )
                BottomTab.History -> HistoryTab()
                BottomTab.Settings -> SettingsTab(
                    onOpenPrivacy = onOpenPrivacy,
                    onOpenTerms = onOpenTerms,
                    onOpenAbout = onOpenAbout,
                    onOpenHelp = onOpenHelp,
                    onOpenTestScreen = onOpenTestScreen,
                )
                BottomTab.Add -> HomeTab()
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
