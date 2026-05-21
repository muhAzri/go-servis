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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import com.zrifapps.goservice.ui.vehicle.components.ComponentInfo
import com.zrifapps.goservice.ui.vehicle.components.ComponentsCatalog

private enum class IntervalMode { Preset, Custom }
private enum class IntervalUnit { Km, Bulan, Keduanya }

@Composable
fun ComponentDetailScreen(
    componentId: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onStopMonitoring: () -> Unit,
    vehicleType: String = "motor",
) {
    val component: ComponentInfo = remember(componentId) {
        ComponentsCatalog.byId(componentId) ?: ComponentsCatalog.all.first()
    }
    val intervalStr = component.intervalFor(vehicleType)

    var mode by remember { mutableStateOf(IntervalMode.Preset) }
    var unit by remember { mutableStateOf(IntervalUnit.Km) }
    var kmVal by remember { mutableStateOf(2000) }
    var monthVal by remember { mutableStateOf(2) }
    var showStopDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        TopBar(onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            HeroCard(component = component)
            Spacer(Modifier.height(16.dp))
            SectionLabel("Interval pengingat")
            ModeSelector(
                mode = mode,
                presetSub = intervalStr,
                onSelect = { mode = it },
            )
            if (mode == IntervalMode.Custom) {
                Spacer(Modifier.height(8.dp))
                CustomIntervalCard(
                    unit = unit,
                    kmVal = kmVal,
                    monthVal = monthVal,
                    onUnit = { unit = it },
                    onKm = { kmVal = it },
                    onMonth = { monthVal = it },
                )
            }
            Spacer(Modifier.height(16.dp))
            SectionLabel("Terakhir diservis")
            LastServiceCard()
            Spacer(Modifier.height(16.dp))
            StopMonitoringButton(onClick = { showStopDialog = true })
            Spacer(Modifier.height(24.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            AppButton(text = "Simpan perubahan", onClick = onSave)
        }
    }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text(text = "Berhenti pantau ${component.label}?") },
            text = {
                Text(text = "Pengingat untuk komponen ini akan dimatikan. Kamu masih bisa mengaktifkannya lagi nanti.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showStopDialog = false
                    onStopMonitoring()
                }) {
                    Text(text = "Berhenti pantau", color = AppColors.Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) { Text("Batal") }
            },
            containerColor = AppColors.Surface,
        )
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircleIconButton(icon = FaIcons.CHEVRON_LEFT, onClick = onBack)
        Text(
            text = "Detail komponen",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
        )
    }
}

@Composable
private fun HeroCard(component: ComponentInfo) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(component.color.copy(alpha = 0.13f), component.color.copy(alpha = 0.03f)),
                ),
            )
            .border(BorderStroke(1.dp, component.color.copy(alpha = 0.2f)), RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(component.color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = component.icon, color = component.color, size = 28.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "KOMPONEN DIPANTAU",
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontFamily = font,
            )
            Text(
                text = component.label,
                color = AppColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                fontFamily = font,
            )
            Text(
                text = component.why,
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    val font = plusJakartaSansFontFamily()
    Text(
        text = text.uppercase(),
        color = AppColors.TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.8.sp,
        fontFamily = font,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
    )
}

@Composable
private fun ModeSelector(
    mode: IntervalMode,
    presetSub: String,
    onSelect: (IntervalMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ModeCard(
            label = "Pakai rekomendasi",
            sub = presetSub,
            selected = mode == IntervalMode.Preset,
            subMonospace = true,
            onClick = { onSelect(IntervalMode.Preset) },
            modifier = Modifier.weight(1f),
        )
        ModeCard(
            label = "Atur sendiri",
            sub = "KM atau tanggal",
            selected = mode == IntervalMode.Custom,
            subMonospace = false,
            onClick = { onSelect(IntervalMode.Custom) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ModeCard(
    label: String,
    sub: String,
    selected: Boolean,
    subMonospace: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val font = plusJakartaSansFontFamily()
    val borderColor = if (selected) AppColors.Primary else AppColors.Border
    val background = if (selected) AppColors.PrimarySofter else AppColors.Surface
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Text(
            text = label,
            color = AppColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
        Text(
            text = sub,
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = if (subMonospace) FontFamily.Monospace else font,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun CustomIntervalCard(
    unit: IntervalUnit,
    kmVal: Int,
    monthVal: Int,
    onUnit: (IntervalUnit) -> Unit,
    onKm: (Int) -> Unit,
    onMonth: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        UnitTabs(unit = unit, onChange = onUnit)
        if (unit == IntervalUnit.Km || unit == IntervalUnit.Keduanya) {
            Spacer(Modifier.height(12.dp))
            StepperField(
                label = "Setiap KM",
                valueText = "${formatThousands(kmVal)} km",
                onMinus = { onKm(maxOf(500, kmVal - 500)) },
                onPlus = { onKm(kmVal + 500) },
            )
        }
        if (unit == IntervalUnit.Bulan || unit == IntervalUnit.Keduanya) {
            Spacer(Modifier.height(12.dp))
            StepperField(
                label = "Setiap bulan",
                valueText = "$monthVal bulan",
                onMinus = { onMonth(maxOf(1, monthVal - 1)) },
                onPlus = { onMonth(monthVal + 1) },
            )
        }
    }
}

@Composable
private fun UnitTabs(unit: IntervalUnit, onChange: (IntervalUnit) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        UnitTab("KM", unit == IntervalUnit.Km, { onChange(IntervalUnit.Km) }, Modifier.weight(1f))
        UnitTab("Bulan", unit == IntervalUnit.Bulan, { onChange(IntervalUnit.Bulan) }, Modifier.weight(1f))
        UnitTab("Keduanya", unit == IntervalUnit.Keduanya, { onChange(IntervalUnit.Keduanya) }, Modifier.weight(1f))
    }
}

@Composable
private fun UnitTab(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) AppColors.Primary else AppColors.SurfaceAlt)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else AppColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
    }
}

@Composable
private fun StepperField(label: String, valueText: String, onMinus: () -> Unit, onPlus: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Column {
        Text(
            text = label,
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.SurfaceAlt)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = valueText,
                color = AppColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f),
            )
            StepperButton(symbol = "−", onClick = onMinus)
            Spacer(Modifier.size(6.dp))
            StepperButton(symbol = "+", onClick = onPlus)
        }
    }
}

@Composable
private fun StepperButton(symbol: String, onClick: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            color = AppColors.TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
    }
}

@Composable
private fun LastServiceCard() {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "20 Feb 2026",
                    color = AppColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
                Text(
                    text = "pada 16.000 km · 2.420 km lalu",
                    color = AppColors.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AppColors.SurfaceAlt)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "AHASS Kebon Jeruk",
                    color = AppColors.TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AppColors.Border),
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FaIcon(icon = FaIcons.BELL, color = AppColors.Primary, size = 12.sp)
            Text(
                text = "Berikutnya: 20.420 km / 6 Juli 2026",
                color = AppColors.TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
            )
        }
    }
}

@Composable
private fun StopMonitoringButton(onClick: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, AppColors.Danger.copy(alpha = 0.4f)), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FaIcon(icon = FaIcons.TRASH, color = AppColors.Danger, size = 14.sp)
        Text(
            text = "Berhenti pantau komponen ini",
            color = AppColors.Danger,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
    }
}

private fun formatThousands(value: Int): String {
    val s = value.toString().reversed().chunked(3).joinToString(".").reversed()
    return s
}
