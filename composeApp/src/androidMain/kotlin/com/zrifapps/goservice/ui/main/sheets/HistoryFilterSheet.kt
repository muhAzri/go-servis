package com.zrifapps.goservice.ui.main.sheets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.MultiPicker
import com.zrifapps.goservice.ui.components.MultiPickerGroup
import com.zrifapps.goservice.ui.components.MultiPickerItem
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import kotlinx.coroutines.launch

data class HistoryFilterValue(
    val vehicleIds: Set<String> = emptySet(),
    val serviceTypeIds: Set<String> = emptySet(),
    val componentIds: Set<String> = emptySet(),
    val workshopIds: Set<String> = emptySet(),
    val dateRangeId: String = "all",
) {
    val isEmpty: Boolean
        get() = vehicleIds.isEmpty() &&
            serviceTypeIds.isEmpty() &&
            componentIds.isEmpty() &&
            workshopIds.isEmpty() &&
            dateRangeId == "all"
}

private val vehicleItems = listOf(
    MultiPickerItem(id = "v1", label = "Beat Hitam", subtitle = "B 4521 KZA", icon = FaIcons.MOTORCYCLE, color = AppColors.Primary),
    MultiPickerItem(id = "v2", label = "Vario Merah", subtitle = "B 6789 SKR", icon = FaIcons.MOTORCYCLE, color = Color(0xFFD6453A)),
    MultiPickerItem(id = "v3", label = "Avanza Putih", subtitle = "B 1234 ABC", icon = FaIcons.CAR, color = Color(0xFF3F4D5C)),
    MultiPickerItem(id = "v4", label = "Brio Biru", subtitle = "B 9876 XYZ", icon = FaIcons.CAR, color = Color(0xFF3FB1D6)),
)

private val serviceTypeItems = listOf(
    MultiPickerItem(id = "oli", label = "Ganti Oli", icon = FaIcons.OIL_CAN, color = Color(0xFFE89C2E)),
    MultiPickerItem(id = "filter", label = "Filter", icon = FaIcons.FILTER, color = Color(0xFF7B6FE8)),
    MultiPickerItem(id = "rem", label = "Kampas Rem", icon = FaIcons.CIRCLE_NOTCH, color = AppColors.Primary),
    MultiPickerItem(id = "ban", label = "Ban", icon = FaIcons.LIFE_RING, color = Color(0xFF3F4D5C)),
    MultiPickerItem(id = "aki", label = "Aki", icon = FaIcons.CAR_BATTERY, color = Color(0xFFD6453A)),
    MultiPickerItem(id = "radiator", label = "Coolant", icon = FaIcons.TEMPERATURE_HALF, color = Color(0xFF3FB1D6)),
)

private val componentItems = listOf(
    MultiPickerItem(id = "c_oil", label = "Oli Mesin", subtitle = "Beat Hitam"),
    MultiPickerItem(id = "c_oilf", label = "Filter Oli", subtitle = "Avanza Putih"),
    MultiPickerItem(id = "c_brake", label = "Kampas Rem Depan", subtitle = "Vario Merah"),
    MultiPickerItem(id = "c_tire", label = "Ban Belakang", subtitle = "Brio Biru"),
)

private val workshopItems = listOf(
    MultiPickerItem(id = "w1", label = "AHASS Sentral"),
    MultiPickerItem(id = "w2", label = "Auto2000 Cibubur"),
    MultiPickerItem(id = "w3", label = "Bengkel Pak Sukar"),
    MultiPickerItem(id = "w4", label = "Yamaha Mekar"),
)

private val dateRanges = listOf(
    "all" to "Semua",
    "month" to "Bulan ini",
    "3m" to "3 bulan",
    "6m" to "6 bulan",
    "1y" to "1 tahun",
    "custom" to "Custom",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryFilterSheet(
    onDismiss: () -> Unit,
    onApply: (HistoryFilterValue) -> Unit,
    initial: HistoryFilterValue = HistoryFilterValue(),
    resultCount: Int = 12,
) {
    val font = plusJakartaSansFontFamily()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var vehicleSel by remember { mutableStateOf(initial.vehicleIds) }
    var serviceSel by remember { mutableStateOf(initial.serviceTypeIds) }
    var componentSel by remember { mutableStateOf(initial.componentIds) }
    var workshopSel by remember { mutableStateOf(initial.workshopIds) }
    var dateRange by remember { mutableStateOf(initial.dateRangeId) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(AppColors.Border),
            )
        },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Filter Riwayat",
                    color = AppColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = font,
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    onClick = {
                        vehicleSel = emptySet()
                        serviceSel = emptySet()
                        componentSel = emptySet()
                        workshopSel = emptySet()
                        dateRange = "all"
                    },
                ) {
                    Text(
                        text = "Reset semua",
                        color = AppColors.Primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                InlineChecklist(
                    title = "KENDARAAN",
                    items = vehicleItems,
                    selected = vehicleSel,
                    onChange = { vehicleSel = it },
                )
                Spacer(Modifier.height(16.dp))

                InlineChecklist(
                    title = "JENIS SERVIS",
                    items = serviceTypeItems,
                    selected = serviceSel,
                    onChange = { serviceSel = it },
                )
                Spacer(Modifier.height(16.dp))

                InlineChecklist(
                    title = "KOMPONEN",
                    items = componentItems,
                    selected = componentSel,
                    onChange = { componentSel = it },
                )
                Spacer(Modifier.height(16.dp))

                InlineChecklist(
                    title = "BENGKEL",
                    items = workshopItems,
                    selected = workshopSel,
                    onChange = { workshopSel = it },
                )
                Spacer(Modifier.height(16.dp))

                SectionLabel("Range tanggal")
                Spacer(Modifier.height(8.dp))
                DateRangeChips(selected = dateRange, onSelect = { dateRange = it })

                Spacer(Modifier.height(20.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Surface)
                    .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(0.dp))
                    .padding(horizontal = 20.dp, vertical = 14.dp),
            ) {
                Button(
                    onClick = {
                        val value = HistoryFilterValue(
                            vehicleIds = vehicleSel,
                            serviceTypeIds = serviceSel,
                            componentIds = componentSel,
                            workshopIds = workshopSel,
                            dateRangeId = dateRange,
                        )
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            onApply(value)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                ) {
                    FaIcon(icon = FaIcons.CHECK, color = Color.White, size = 13.sp)
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "Terapkan ($resultCount hasil)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font,
                    )
                }
            }
        }
    }
}

@Composable
private fun InlineChecklist(
    title: String,
    items: List<MultiPickerItem>,
    selected: Set<String>,
    onChange: (Set<String>) -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionLabel(title)
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(14.dp))
                .padding(vertical = 4.dp),
        ) {
            items.forEachIndexed { i, item ->
                val picked = item.id in selected
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onChange(
                                if (picked) selected - item.id else selected + item.id,
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (picked) AppColors.Primary else Color.Transparent)
                            .border(
                                BorderStroke(2.dp, if (picked) AppColors.Primary else AppColors.Border),
                                RoundedCornerShape(6.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (picked) {
                            FaIcon(icon = FaIcons.CHECK, color = Color.White, size = 12.sp)
                        }
                    }
                    if (item.icon != null) {
                        FaIcon(
                            icon = item.icon,
                            color = item.color ?: AppColors.TextMuted,
                            size = 16.sp,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.label,
                            color = AppColors.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = font,
                        )
                        if (item.subtitle != null) {
                            Text(
                                text = item.subtitle,
                                color = AppColors.TextMuted,
                                fontSize = 11.sp,
                                fontFamily = font,
                            )
                        }
                    }
                }
                if (i < items.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 48.dp)
                            .height(1.dp)
                            .background(AppColors.Border),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = AppColors.TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        fontFamily = plusJakartaSansFontFamily(),
    )
}

@Composable
private fun DateRangeChips(selected: String, onSelect: (String) -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        dateRanges.forEach { (id, label) ->
            val active = id == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (active) AppColors.PrimarySoft else AppColors.SurfaceAlt)
                    .border(
                        BorderStroke(
                            1.dp,
                            if (active) AppColors.Primary else AppColors.Border,
                        ),
                        RoundedCornerShape(100.dp),
                    )
                    .clickable { onSelect(id) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (active) AppColors.Primary else AppColors.TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}
