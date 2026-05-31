package com.zrifapps.goservice.ui.service

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.service.presentation.ServiceDetailViewModel
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.service.components.formatRupiah
import com.zrifapps.goservice.ui.service.components.formatKmDisplay
import com.zrifapps.goservice.ui.service.components.formatServiceDate
import com.zrifapps.goservice.ui.service.components.serviceTypeMeta
import com.zrifapps.goservice.ui.service.components.trackedComponentMeta
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun ServiceDetailScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit = {},
    @Suppress("UNUSED_PARAMETER") onDelete: () -> Unit = {},
    onOpenNextReminder: () -> Unit = {},
    recordId: String,
    vm: ServiceDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(recordId) { vm.load(recordId) }
    val state by vm.state.collectAsStateWithLifecycle()
    val record = state.record
    val vehicle = state.vehicle

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
                .verticalScroll(rememberScrollState()),
        ) {
            if (record == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (state.isLoading) "Memuat…" else "Catatan tidak ditemukan",
                        color = AppColors.TextMuted,
                        fontFamily = plusJakartaSansFontFamily(),
                        fontSize = 14.sp,
                    )
                }
                return@Column
            }

            val meta = serviceTypeMeta(record.serviceType.key)
            val typeLabel = meta.label.replace("\n", " ")
            val vehicleLine = vehicle?.let {
                val plate = it.plateNumber.takeIf(String::isNotBlank)
                if (plate != null) "${it.displayTitle} · $plate" else it.displayTitle
            } ?: "Kendaraan dihapus"

            HeaderRow(
                icon = meta.icon,
                accent = meta.color,
                title = typeLabel,
                subtitle = vehicleLine,
            )
            Spacer(Modifier.height(16.dp))
            FactsCard(
                dateLabel = formatServiceDate(record.serviceDate),
                kmLabel = formatKmDisplay(record.odometer.kilometers),
                workshopLabel = record.workshop?.takeIf(String::isNotBlank) ?: "—",
                costLabel = formatRupiah(record.cost.amountIdr),
            )
            Spacer(Modifier.height(20.dp))

            val note = record.note?.takeIf(String::isNotBlank)
            if (note != null) {
                SectionLabel("Catatan")
                NoteBlock(text = note)
                Spacer(Modifier.height(20.dp))
            }

            if (record.componentIds.isNotEmpty()) {
                SectionLabel("Komponen yang diservis")
                ComponentList(componentIds = record.componentIds)
                Spacer(Modifier.height(20.dp))
            }

            ActionRow(onEdit = onEdit)
            Spacer(Modifier.height(24.dp))
        }
    }

    // Reminder-next card requires reminder lookup — wired in Reminder phase.
    @Suppress("UNUSED_EXPRESSION") onOpenNextReminder
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircleIconButton(icon = FaIcons.CHEVRON_LEFT, onClick = onBack)
        Text(
            text = "Detail Servis",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HeaderRow(
    icon: String,
    accent: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        IconBadge(
            icon = icon,
            foreground = accent,
            background = accent.copy(alpha = 0.15f),
            size = 56.dp,
            iconSize = 28.sp,
            corner = 16.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                fontFamily = font,
            )
            Text(
                text = subtitle,
                color = AppColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun FactsCard(
    dateLabel: String,
    kmLabel: String,
    workshopLabel: String,
    costLabel: String,
) {
    val font = plusJakartaSansFontFamily()
    val rows = listOf(
        Triple(FaIcons.CALENDAR, dateLabel, false),
        Triple(FaIcons.GAUGE, kmLabel, true),
        Triple(FaIcons.LOCATION_DOT, workshopLabel, false),
        Triple(FaIcons.OIL_CAN, costLabel, true),
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp)),
    ) {
        rows.forEachIndexed { index, (icon, value, mono) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                FaIcon(icon = icon, color = AppColors.TextMuted, size = 16.sp)
                Text(
                    text = value,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = if (mono) FontFamily.Monospace else font,
                )
            }
            if (index < rows.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(AppColors.Border),
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
        letterSpacing = 1.sp,
        fontFamily = font,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp),
    )
}

@Composable
private fun NoteBlock(text: String) {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = text,
            color = AppColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            lineHeight = 21.sp,
        )
    }
}

@Composable
private fun ComponentList(componentIds: List<String>) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp)),
    ) {
        componentIds.forEachIndexed { index, id ->
            val item = trackedComponentMeta(id)
            val label = item?.label ?: id
            val icon = item?.icon ?: FaIcons.WRENCH
            val color = item?.color ?: AppColors.Primary

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IconBadge(
                    icon = icon,
                    foreground = color,
                    background = color.copy(alpha = 0.13f),
                    size = 36.dp,
                    iconSize = 16.sp,
                    corner = 10.dp,
                )
                Text(
                    text = label,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                    modifier = Modifier.weight(1f),
                )
            }
            if (index < componentIds.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(AppColors.Border),
                )
            }
        }
    }
}

@Composable
private fun ActionRow(onEdit: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
                .clickable(onClick = onEdit),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FaIcon(icon = FaIcons.PEN_TO_SQUARE, color = AppColors.TextPrimary, size = 13.sp)
                Text(
                    text = "Edit",
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}
