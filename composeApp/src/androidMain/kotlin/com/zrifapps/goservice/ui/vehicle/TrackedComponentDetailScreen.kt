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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent
import com.zrifapps.goservice.feature.component.presentation.TrackedComponentDetailViewModel
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import com.zrifapps.goservice.ui.vehicle.components.faIcon
import com.zrifapps.goservice.ui.vehicle.components.uiColor
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrackedComponentDetailScreen(
    trackedId: String,
    onBack: () -> Unit,
    onStopped: () -> Unit,
    onLogServiceForComponent: () -> Unit = {},
    onCreateReminderForComponent: () -> Unit = {},
    vm: TrackedComponentDetailViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(trackedId) { vm.load(trackedId) }
    DisposableEffect(vm) {
        val cancellable = vm.observeEvents { event ->
            when (event) {
                is TrackedComponentDetailViewModel.Event.Saved -> Unit
                is TrackedComponentDetailViewModel.Event.Stopped -> onStopped()
                is TrackedComponentDetailViewModel.Event.Failed -> Unit
            }
        }
        onDispose { cancellable.cancel() }
    }

    var showStopDialog by remember { mutableStateOf(false) }

    val tracked = state.tracked
    val catalog = state.catalog

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
            if (tracked == null) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = if (state.isLoading) "Memuat…" else "Komponen tidak ditemukan",
                    color = AppColors.TextMuted,
                    fontSize = 13.sp,
                )
                return@Column
            }
            HeroCard(tracked = tracked, catalog = catalog)
            Spacer(Modifier.height(16.dp))
            SectionLabel("Interval pengingat")
            ModeSelector(
                mode = state.mode,
                presetSub = catalog?.let { presetIntervalLabel(tracked, it) } ?: "—",
                onSelect = vm::setMode,
            )
            if (state.mode == TrackedComponentDetailViewModel.IntervalMode.Custom) {
                Spacer(Modifier.height(8.dp))
                CustomIntervalCard(
                    kmVal = state.customKmOverride,
                    monthVal = state.customDaysOverride?.let { it / 30 },
                    onKm = vm::setCustomKm,
                    onMonth = { months -> vm.setCustomDays(months?.let { it * 30 }) },
                )
            }
            Spacer(Modifier.height(16.dp))
            SectionLabel("Terakhir diservis")
            LastServiceCard(tracked = tracked)
            Spacer(Modifier.height(20.dp))
            ComponentCtaSection(
                onLogService = onLogServiceForComponent,
                onCreateReminder = onCreateReminderForComponent,
            )
            Spacer(Modifier.height(20.dp))
            StopMonitoringButton(onClick = { showStopDialog = true })
            Spacer(Modifier.height(24.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            AppButton(
                text = if (state.isSaving) "Menyimpan…" else "Simpan perubahan",
                onClick = vm::save,
            )
        }
    }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text(text = "Berhenti pantau ${catalog?.label ?: "komponen"}?") },
            text = {
                Text(text = "Pengingat untuk komponen ini akan dimatikan. Kamu masih bisa mengaktifkannya lagi nanti.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showStopDialog = false
                    vm.stopMonitoring()
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
private fun HeroCard(tracked: TrackedComponent, catalog: Component?) {
    val font = plusJakartaSansFontFamily()
    val color = catalog?.uiColor() ?: AppColors.Primary
    val icon = catalog?.faIcon() ?: FaIcons.WRENCH
    val label = tracked.displayName(catalog)
    val why = catalog?.why.orEmpty()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(color.copy(alpha = 0.13f), color.copy(alpha = 0.03f)),
                ),
            )
            .border(BorderStroke(1.dp, color.copy(alpha = 0.2f)), RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = icon, color = color, size = 28.sp)
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
                text = label,
                color = AppColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                fontFamily = font,
            )
            if (why.isNotBlank()) {
                Text(
                    text = why,
                    color = AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
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
    mode: TrackedComponentDetailViewModel.IntervalMode,
    presetSub: String,
    onSelect: (TrackedComponentDetailViewModel.IntervalMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ModeCard(
            label = "Pakai rekomendasi",
            sub = presetSub,
            selected = mode == TrackedComponentDetailViewModel.IntervalMode.Preset,
            subMonospace = true,
            onClick = { onSelect(TrackedComponentDetailViewModel.IntervalMode.Preset) },
            modifier = Modifier.weight(1f),
        )
        ModeCard(
            label = "Atur sendiri",
            sub = "KM atau bulan",
            selected = mode == TrackedComponentDetailViewModel.IntervalMode.Custom,
            subMonospace = false,
            onClick = { onSelect(TrackedComponentDetailViewModel.IntervalMode.Custom) },
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
    kmVal: Long?,
    monthVal: Int?,
    onKm: (Long?) -> Unit,
    onMonth: (Int?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        StepperField(
            label = "Setiap KM (kosongkan untuk tidak pakai)",
            valueText = kmVal?.let { "${formatThousands(it.toInt())} km" } ?: "—",
            onMinus = {
                val next = ((kmVal ?: 0L) - 500L).coerceAtLeast(0L)
                onKm(if (next == 0L) null else next)
            },
            onPlus = { onKm((kmVal ?: 0L) + 500L) },
        )
        Spacer(Modifier.height(12.dp))
        StepperField(
            label = "Setiap bulan (opsional)",
            valueText = monthVal?.let { "$it bulan" } ?: "—",
            onMinus = {
                val next = ((monthVal ?: 0) - 1).coerceAtLeast(0)
                onMonth(if (next == 0) null else next)
            },
            onPlus = { onMonth((monthVal ?: 0) + 1) },
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
private fun LastServiceCard(tracked: TrackedComponent) {
    val font = plusJakartaSansFontFamily()
    val dateLabel = tracked.lastServiceDate?.let { dateFormat.format(Date(it)) } ?: "belum tercatat"
    val kmLabel = tracked.lastServiceOdometer?.kilometers?.let { "${formatThousands(it.toInt())} km" }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Text(
            text = dateLabel,
            color = AppColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
        if (kmLabel != null) {
            Text(
                text = "pada $kmLabel",
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun ComponentCtaSection(
    onLogService: () -> Unit,
    onCreateReminder: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Primary)
                .clickable(onClick = onLogService),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FaIcon(icon = FaIcons.PLUS, color = Color.White, size = 16.sp)
                Text(
                    text = "Catat servis untuk komponen ini",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(modifier = Modifier.weight(1f).height(1.dp).background(AppColors.Border))
            Text(
                text = "atau",
                color = AppColors.TextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
            )
            Box(modifier = Modifier.weight(1f).height(1.dp).background(AppColors.Border))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Transparent)
                .border(BorderStroke(1.5.dp, AppColors.Primary), RoundedCornerShape(14.dp))
                .clickable(onClick = onCreateReminder),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            FaIcon(icon = FaIcons.BELL, color = AppColors.Primary, size = 14.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Buat pengingat manual",
                color = AppColors.Primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
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

private val dateFormat: SimpleDateFormat by lazy {
    SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID"))
}

private fun presetIntervalLabel(tracked: TrackedComponent, catalog: Component): String {
    val motor = catalog.intervalMotor?.displayLabel
    val mobil = catalog.intervalMobil?.displayLabel
    return listOfNotNull(motor, mobil).firstOrNull() ?: "—"
}

private fun formatThousands(value: Int): String {
    if (value == 0) return "0"
    val abs = kotlin.math.abs(value).toString()
    val grouped = abs.reversed().chunked(3).joinToString(".").reversed()
    return if (value < 0) "-$grouped" else grouped
}
