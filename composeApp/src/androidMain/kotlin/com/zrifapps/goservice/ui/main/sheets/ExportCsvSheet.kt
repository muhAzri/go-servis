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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import kotlinx.coroutines.launch

private data class ExportSection(val id: String, val label: String)

private val exportSections = listOf(
    ExportSection("vehicles", "Kendaraan"),
    ExportSection("services", "Riwayat servis"),
    ExportSection("reminders", "Pengingat"),
    ExportSection("components", "Komponen"),
)

private val exportRanges = listOf(
    "all" to "Semua waktu",
    "month" to "Bulan ini",
    "3m" to "3 bulan",
    "1y" to "1 tahun",
    "custom" to "Custom",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportCsvSheet(
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    vehicleCount: Int = 4,
    serviceCount: Int = 12,
    reminderCount: Int = 6,
) {
    val font = plusJakartaSansFontFamily()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var selectedSections by remember {
        mutableStateOf(exportSections.map { it.id }.toSet())
    }
    var selectedRange by remember { mutableStateOf("all") }

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = "Ekspor Data",
                color = AppColors.TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = font,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$vehicleCount kendaraan · $serviceCount servis · $reminderCount pengingat",
                color = AppColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )

            Spacer(Modifier.height(18.dp))
            SectionLabel("Sertakan data")
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                exportSections.forEach { section ->
                    val checked = section.id in selectedSections
                    CheckboxRow(
                        label = section.label,
                        checked = checked,
                        onToggle = {
                            selectedSections = if (checked) {
                                selectedSections - section.id
                            } else {
                                selectedSections + section.id
                            }
                        },
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            SectionLabel("Rentang waktu")
            Spacer(Modifier.height(8.dp))
            RangeChips(selected = selectedRange, onSelect = { selectedRange = it })

            Spacer(Modifier.height(18.dp))
            SectionLabel("File preview")
            Spacer(Modifier.height(8.dp))
            FilePreviewRow(filename = "servisgo-2026-05.csv", meta = "~14 KB · CSV")

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onShare()
                        onDismiss()
                    }
                },
                enabled = selectedSections.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    disabledContainerColor = AppColors.Primary.copy(alpha = 0.35f),
                ),
            ) {
                FaIcon(icon = FaIcons.SHARE, color = Color.White, size = 14.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Bagikan CSV",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Batal",
                    color = AppColors.TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                )
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
private fun CheckboxRow(label: String, checked: Boolean, onToggle: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.SurfaceAlt)
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (checked) AppColors.Primary else Color.Transparent)
                .border(
                    1.5.dp,
                    if (checked) AppColors.Primary else AppColors.Border,
                    RoundedCornerShape(6.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                FaIcon(icon = FaIcons.CHECK, color = Color.White, size = 11.sp)
            }
        }
        Text(
            text = label,
            color = AppColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun RangeChips(selected: String, onSelect: (String) -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        exportRanges.forEach { (id, label) ->
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
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}

@Composable
private fun FilePreviewRow(filename: String, meta: String) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.SurfaceAlt)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppColors.PrimarySoft),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = FaIcons.FILE, color = AppColors.Primary, size = 18.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = filename,
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                text = meta,
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
        }
    }
}
