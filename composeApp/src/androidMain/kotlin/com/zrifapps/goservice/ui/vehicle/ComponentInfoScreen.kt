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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.component.presentation.ComponentInfoViewModel
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.ui.common.toastError
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import com.zrifapps.goservice.ui.vehicle.components.faIcon
import com.zrifapps.goservice.ui.vehicle.components.intervalLabelFor
import com.zrifapps.goservice.ui.vehicle.components.uiColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun ComponentInfoScreen(
    vehicleId: String,
    catalogId: String? = null,
    customName: String? = null,
    onBack: () -> Unit,
    onTracked: (String) -> Unit,
    vm: ComponentInfoViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    LaunchedEffect(catalogId, customName, vehicleId) {
        if (customName != null) vm.loadNewCustom(customName, vehicleId)
        else if (catalogId != null) vm.load(catalogId, vehicleId)
    }
    DisposableEffect(vm) {
        val cancellable = vm.observeEvents { event ->
            when (event) {
                is ComponentInfoViewModel.Event.Tracked -> onTracked(event.trackedId)
                is ComponentInfoViewModel.Event.Failed -> ctx.toastError(event.error)
            }
        }
        onDispose { cancellable.cancel() }
    }

    val catalog = state.catalog
    val isSubmitting = state.isSubmitting

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
            if (catalog == null) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = if (state.isLoading) "Memuat…" else "Komponen tidak ditemukan",
                    color = AppColors.TextMuted,
                    fontSize = 13.sp,
                )
                return@Column
            }
            HeroCard(catalog = catalog)
            Spacer(Modifier.height(16.dp))

            if (!state.requiresCustomInterval) {
                SectionLabel("Interval pabrikan")
                IntervalCard(motor = catalog.intervalLabelFor(VehicleType.Motor), mobil = catalog.intervalLabelFor(VehicleType.Mobil))
            }

            if (state.existingTrackedId == null) {
                if (!state.requiresCustomInterval) {
                    Spacer(Modifier.height(16.dp))
                    SectionLabel("Interval pengingat")
                    ModeSelector(
                        mode = state.mode,
                        presetSub = catalog.intervalLabelFor(VehicleType.Motor).takeIf { it != "—" }
                            ?: catalog.intervalLabelFor(VehicleType.Mobil),
                        onSelect = vm::setMode,
                    )
                } else {
                    SectionLabel("Atur interval pengingat")
                    Text(
                        text = "Komponen kustom belum punya interval pabrikan — tentukan sendiri kapan harus diingatkan.",
                        color = AppColors.TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                    )
                }
                if (state.mode == ComponentInfoViewModel.IntervalMode.Custom) {
                    Spacer(Modifier.height(8.dp))
                    CustomIntervalCard(
                        kmVal = state.customKm,
                        monthVal = state.customDays?.let { it / 30 },
                        onKm = vm::setCustomKm,
                        onMonth = { months -> vm.setCustomDays(months?.let { it * 30 }) },
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        BottomBar(
            isSubmitting = isSubmitting,
            canTrack = state.canTrack,
            existingTrackedId = state.existingTrackedId,
            onTrack = vm::track,
            onOpenTracked = { id -> onTracked(id) },
        )
    }
}

@Composable
private fun ModeSelector(
    mode: ComponentInfoViewModel.IntervalMode,
    presetSub: String,
    onSelect: (ComponentInfoViewModel.IntervalMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ModeCard(
            label = "Pakai rekomendasi",
            sub = presetSub,
            selected = mode == ComponentInfoViewModel.IntervalMode.Preset,
            subMonospace = true,
            onClick = { onSelect(ComponentInfoViewModel.IntervalMode.Preset) },
            modifier = Modifier.weight(1f),
        )
        ModeCard(
            label = "Atur sendiri",
            sub = "KM atau bulan",
            selected = mode == ComponentInfoViewModel.IntervalMode.Custom,
            subMonospace = false,
            onClick = { onSelect(ComponentInfoViewModel.IntervalMode.Custom) },
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
            label = "Setiap KM",
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

private fun formatThousands(value: Int): String {
    if (value == 0) return "0"
    val abs = kotlin.math.abs(value).toString()
    val grouped = abs.reversed().chunked(3).joinToString(".").reversed()
    return if (value < 0) "-$grouped" else grouped
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
            text = "Info komponen",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
        )
    }
}

@Composable
private fun HeroCard(catalog: com.zrifapps.goservice.feature.component.domain.model.Component) {
    val font = plusJakartaSansFontFamily()
    val color = catalog.uiColor()
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
            FaIcon(icon = catalog.faIcon(), color = color, size = 28.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "KOMPONEN",
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontFamily = font,
            )
            Text(
                text = catalog.label,
                color = AppColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                fontFamily = font,
            )
            if (catalog.why.isNotBlank()) {
                Text(
                    text = catalog.why,
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
private fun IntervalCard(motor: String, mobil: String) {
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
            Text(
                text = "Motor",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = motor,
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
        }
        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppColors.Border))
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Mobil",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = mobil,
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
private fun BottomBar(
    isSubmitting: Boolean,
    canTrack: Boolean,
    existingTrackedId: String?,
    onTrack: () -> Unit,
    onOpenTracked: (String) -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val alreadyTracked = existingTrackedId != null
    val enabled = !isSubmitting && (alreadyTracked || canTrack)
    val label = when {
        alreadyTracked -> "Buka pengaturan komponen"
        isSubmitting -> "Menyimpan…"
        !canTrack -> "Atur interval dulu"
        else -> "Pantau komponen ini"
    }
    val icon = if (alreadyTracked) FaIcons.GEAR else FaIcons.PLUS
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (enabled) AppColors.Primary else AppColors.Primary.copy(alpha = 0.35f))
                .clickable(enabled = enabled) {
                    if (alreadyTracked && existingTrackedId != null) onOpenTracked(existingTrackedId) else onTrack()
                },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FaIcon(icon = icon, color = Color.White, size = 16.sp)
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}
