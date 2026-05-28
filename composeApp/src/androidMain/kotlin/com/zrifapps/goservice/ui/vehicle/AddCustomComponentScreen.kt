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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.component.domain.model.ComponentTag
import com.zrifapps.goservice.feature.component.presentation.AddTrackedComponentViewModel
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import com.zrifapps.goservice.ui.vehicle.components.VehicleSubtypes
import com.zrifapps.goservice.ui.vehicle.components.faIcon
import com.zrifapps.goservice.ui.vehicle.components.intervalLabelFor
import com.zrifapps.goservice.ui.vehicle.components.label
import com.zrifapps.goservice.ui.vehicle.components.uiColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddCustomComponentScreen(
    vehicleId: String,
    onBack: () -> Unit,
    onOpenComponent: (String) -> Unit,
    onCreateCustom: (String) -> Unit,
    vm: AddTrackedComponentViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(vehicleId) { vm.load(vehicleId) }

    val vehicle = state.vehicle
    val vehicleType = vehicle?.type ?: VehicleType.Motor
    val subtypeId = vehicle?.subtypeId?.takeIf { it.isNotBlank() && it != "*" }
        ?: VehicleSubtypes.defaultFor(vehicleType.key)
    val subLabel = VehicleSubtypes.labelOf(vehicleType.key, subtypeId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState()),
    ) {
        TopBar(onBack = onBack)
        SearchField(query = state.query, onChange = vm::setQuery)
        Spacer(Modifier.height(4.dp))

        if (state.query.isBlank()) {
            SectionLabel("Rekomendasi untuk $subLabel")
        } else {
            SectionLabel("${state.items.size} hasil")
        }

        if (state.items.isEmpty() && !state.showCustomPrompt) {
            Box(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (state.isLoading) "Memuat…" else "Tidak ada komponen.",
                    color = AppColors.TextMuted,
                    fontSize = 13.sp,
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.items.forEach { item ->
                    ComponentPickRow(
                        item = item,
                        vehicleType = vehicleType,
                        onClick = { if (!item.alreadyTracked) onOpenComponent(item.component.id) },
                    )
                }
            }
        }

        if (state.showCustomPrompt) {
            Spacer(Modifier.height(8.dp))
            CustomPromptRow(query = state.query, onAdd = { onCreateCustom(state.query) })
        }

        Spacer(Modifier.height(16.dp))
        FreeformHint()
        Spacer(Modifier.height(24.dp))
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
            text = "Tambah komponen",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
        )
    }
}

@Composable
private fun SearchField(query: String, onChange: (String) -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FaIcon(icon = FaIcons.SEARCH, color = AppColors.TextMuted, size = 16.sp)
        Box(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = query,
                onValueChange = onChange,
                singleLine = true,
                cursorBrush = SolidColor(AppColors.Primary),
                textStyle = TextStyle(
                    color = AppColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                ),
            )
            if (query.isEmpty()) {
                Text(
                    text = "Cari komponen lain…",
                    color = AppColors.TextSubtle,
                    fontSize = 15.sp,
                    fontFamily = font,
                )
            }
        }
        if (query.isNotEmpty()) {
            Box(
                modifier = Modifier.size(20.dp).clickable { onChange("") },
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = FaIcons.XMARK, color = AppColors.TextMuted, size = 14.sp)
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
        modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 8.dp),
    )
}

@Composable
private fun ComponentPickRow(
    item: AddTrackedComponentViewModel.Item,
    vehicleType: VehicleType,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val component = item.component
    val color = component.uiColor()
    val locked = item.alreadyTracked
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .clickable(enabled = !locked, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(
            icon = component.faIcon(),
            foreground = color,
            background = color.copy(alpha = 0.13f),
            size = 38.dp,
            iconSize = 18.sp,
            corner = 10.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = component.label,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
                TagChip(tag = component.tag)
            }
            Text(
                text = if (locked) "Sudah dipantau" else component.intervalLabelFor(vehicleType),
                color = if (locked) AppColors.Primary else AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = if (locked) font else FontFamily.Monospace,
            )
        }
        if (locked) {
            FaIcon(icon = FaIcons.CHECK, color = AppColors.Primary, size = 14.sp)
        } else {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(AppColors.PrimarySoft),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = FaIcons.PLUS, color = AppColors.Primary, size = 14.sp)
            }
        }
    }
}

@Composable
private fun CustomPromptRow(query: String, onAdd: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .clickable(onClick = onAdd)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(AppColors.Primary),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = FaIcons.PLUS, color = Color.White, size = 14.sp)
        }
        Text(
            text = "Tambahkan \"$query\" sebagai komponen baru",
            color = AppColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
    }
}

@Composable
private fun TagChip(tag: ComponentTag) {
    val font = plusJakartaSansFontFamily()
    val (fg, bg) = when (tag) {
        ComponentTag.Core -> AppColors.Primary to AppColors.PrimarySoft
        ComponentTag.Plus -> AppColors.Warning to AppColors.WarningSoft
        ComponentTag.Pro -> AppColors.TextMuted to AppColors.SurfaceAlt
    }
    Box(
        modifier = Modifier.clip(CircleShape).background(bg).padding(horizontal = 7.dp, vertical = 2.dp),
    ) {
        Text(
            text = tag.label().uppercase(),
            color = fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            fontFamily = font,
        )
    }
}

@Composable
private fun FreeformHint() {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.SurfaceAlt)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FaIcon(icon = FaIcons.LIGHTBULB, color = AppColors.TextMuted, size = 14.sp)
        Column {
            Text(
                text = "Pilih satu untuk atur interval",
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = "Tiap komponen bisa kamu atur interval km / bulannya sebelum dipantau.",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
