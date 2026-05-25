package com.zrifapps.goservice.ui.vehicle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleListViewModel
import com.zrifapps.goservice.ui.components.ActionSheet
import com.zrifapps.goservice.ui.components.ActionSheetOption
import com.zrifapps.goservice.ui.components.ActionSheetSelectionMode
import com.zrifapps.goservice.ui.components.DetailToolbar
import com.zrifapps.goservice.ui.components.DetailToolbarAction
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.SortChip
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

private val sortOptions = listOf(
    "input" to "Urutan input",
    "az" to "A → Z",
    "km_asc" to "KM terendah",
    "km_desc" to "KM tertinggi",
    "status" to "Status",
)

@Composable
fun VehicleListScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onOpenVehicle: (String) -> Unit = {},
    onAddVehicle: () -> Unit = {},
    vm: VehicleListViewModel = koinViewModel(),
) {
    val font = plusJakartaSansFontFamily()
    var sortValue by remember { mutableStateOf("input") }
    var showSortSheet by remember { mutableStateOf(false) }
    val sortLabel = sortOptions.firstOrNull { it.first == sortValue }?.second ?: "Urutan input"
    val state by vm.state.collectAsStateWithLifecycle()
    val vehicles = remember(state.vehicles, sortValue) { sortVehicles(state.vehicles, sortValue) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailToolbar(
                title = "Garasi Saya",
                onBack = onBack,
                actions = listOf(
                    DetailToolbarAction(icon = FaIcons.PLUS, onTap = onAddVehicle),
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${vehicles.size} kendaraan",
                    color = AppColors.TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                    modifier = Modifier.weight(1f),
                )
                SortChip(
                    label = sortLabel,
                    onClick = { showSortSheet = true },
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    top = 4.dp,
                    bottom = 96.dp,
                ),
            ) {
                items(vehicles) { item ->
                    VehicleListRow(item = item, onClick = { onOpenVehicle(item.id) })
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(20.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(AppColors.Primary)
                .clickable(onClick = onAddVehicle)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FaIcon(icon = FaIcons.PLUS, color = Color.White, size = 14.sp)
            Text(
                text = "Tambah Kendaraan",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }

    if (showSortSheet) {
        ActionSheet(
            title = "Urutkan kendaraan",
            options = sortOptions.map { (id, label) ->
                ActionSheetOption(value = id, label = label)
            },
            selectionMode = ActionSheetSelectionMode.Radio,
            initiallySelected = sortValue,
            primaryLabel = "Terapkan",
            onPrimary = { sortValue = it },
            onDismiss = { showSortSheet = false },
            onSelect = {},
        )
    }
}

@Composable
private fun VehicleListRow(item: Vehicle, onClick: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    val accent = remember(item.color.value) {
        runCatching { Color(android.graphics.Color.parseColor(item.color.value)) }
            .getOrDefault(AppColors.Primary)
    }
    val icon = if (item.type == VehicleType.Mobil) FaIcons.CAR else FaIcons.MOTORCYCLE
    val plateAndKm = buildString {
        if (item.plateNumber.isNotBlank()) {
            append(item.plateNumber)
            append(" · ")
        }
        append(formatOdometerKm(item.odometer.kilometers))
        append(" km")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(
            icon = icon,
            foreground = accent,
            background = accent.copy(alpha = 0.13f),
            size = 48.dp,
            iconSize = 24.sp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.displayTitle,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = plateAndKm,
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
        FaIcon(icon = FaIcons.CHEVRON_RIGHT, color = AppColors.TextSubtle, size = 14.sp)
    }
}

private fun sortVehicles(list: List<Vehicle>, sortKey: String): List<Vehicle> = when (sortKey) {
    "az" -> list.sortedBy { it.displayTitle.lowercase() }
    "km_asc" -> list.sortedBy { it.odometer.kilometers }
    "km_desc" -> list.sortedByDescending { it.odometer.kilometers }
    else -> list
}

private fun formatOdometerKm(km: Long): String {
    val abs = kotlin.math.abs(km).toString()
    val grouped = abs.reversed().chunked(3).joinToString(".").reversed()
    return if (km < 0) "-$grouped" else grouped
}
