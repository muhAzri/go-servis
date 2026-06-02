package com.zrifapps.goservice.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleListViewModel
import com.zrifapps.goservice.ui.components.BottomNavBar
import com.zrifapps.goservice.ui.components.BottomTab
import com.zrifapps.goservice.ui.main.tabs.HistoryTab
import com.zrifapps.goservice.ui.main.tabs.HomeTab
import com.zrifapps.goservice.ui.main.tabs.RemindersTab
import com.zrifapps.goservice.ui.main.tabs.SettingsTab
import com.zrifapps.goservice.ui.theme.AppColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainTabsScreen(
    userName: String = "",
    onAddService: () -> Unit = {},
    onAddVehicle: () -> Unit = {},
    onUpdateOdometer: () -> Unit = {},
    onOpenReminderDetail: (String) -> Unit = {},
    onOpenVehicleDetail: () -> Unit = {},
    onOpenServiceDetail: (String) -> Unit = {},
    onOpenAddReminder: () -> Unit = {},
    onOpenPrivacy: () -> Unit = {},
    onOpenTerms: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenEditProfile: () -> Unit = {},
    onOpenVehicleList: () -> Unit = {},
    vehicleVm: VehicleListViewModel = koinViewModel(),
) {
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }
    var showNoVehiclePrompt by remember { mutableStateOf(false) }
    val vehicleState by vehicleVm.state.collectAsStateWithLifecycle()

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
                    userName = userName,
                    onOpenReminders = { selectedTab = BottomTab.Reminders },
                    onOpenReminderDetail = onOpenReminderDetail,
                    onOpenVehicleDetail = onOpenVehicleDetail,
                    onOpenVehicleList = onOpenVehicleList,
                    onAddService = onAddService,
                    onAddVehicle = onAddVehicle,
                    onUpdateOdometer = onUpdateOdometer,
                )
                BottomTab.Reminders -> RemindersTab(
                    onOpenReminderDetail = onOpenReminderDetail,
                    onAddReminder = onOpenAddReminder,
                )
                BottomTab.History -> HistoryTab(
                    onOpenServiceDetail = onOpenServiceDetail,
                    onAddService = onAddService,
                )
                BottomTab.Settings -> SettingsTab(
                    onOpenPrivacy = onOpenPrivacy,
                    onOpenTerms = onOpenTerms,
                    onOpenAbout = onOpenAbout,
                    onOpenHelp = onOpenHelp,
                    onOpenEditProfile = onOpenEditProfile,
                )
                BottomTab.Add -> HomeTab(userName = userName)
            }
        }
        BottomNavBar(
            selected = selectedTab,
            onSelect = { tab ->
                if (tab == BottomTab.Add) {
                    if (vehicleState.vehicles.isEmpty() && !vehicleState.isLoading) {
                        showNoVehiclePrompt = true
                    } else {
                        onAddService()
                    }
                } else {
                    selectedTab = tab
                }
            },
        )
    }

    if (showNoVehiclePrompt) {
        AlertDialog(
            onDismissRequest = { showNoVehiclePrompt = false },
            title = { Text("Tambah kendaraan dulu") },
            text = {
                Text("Belum ada kendaraan untuk dicatatkan servisnya. Tambah kendaraan dulu yuk?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showNoVehiclePrompt = false
                    onAddVehicle()
                }) { Text("Tambah Kendaraan") }
            },
            dismissButton = {
                TextButton(onClick = { showNoVehiclePrompt = false }) { Text("Batal") }
            },
        )
    }
}
