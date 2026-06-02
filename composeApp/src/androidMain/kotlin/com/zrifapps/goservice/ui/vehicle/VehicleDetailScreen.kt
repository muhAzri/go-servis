package com.zrifapps.goservice.ui.vehicle

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.zrifapps.goservice.feature.component.domain.model.ComponentUrgency
import com.zrifapps.goservice.feature.component.presentation.VehicleComponentsViewModel
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleDetailViewModel
import com.zrifapps.goservice.ui.service.components.formatRupiah
import com.zrifapps.goservice.ui.service.components.formatServiceDate
import com.zrifapps.goservice.ui.service.components.serviceTypeMeta
import com.zrifapps.goservice.ui.service.components.trackedComponentMeta
import com.zrifapps.goservice.ui.vehicle.components.faIcon
import com.zrifapps.goservice.ui.vehicle.components.intervalLabelFor
import com.zrifapps.goservice.ui.vehicle.components.uiColor
import com.zrifapps.goservice.ui.components.AdBannerSlot
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.ReminderUrgency
import com.zrifapps.goservice.ui.components.StatusDot
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import com.zrifapps.goservice.ui.vehicle.components.SubtypeBadge
import com.zrifapps.goservice.ui.vehicle.components.VehicleSubtypes
import com.zrifapps.goservice.ui.vehicle.components.dashedBorder
import androidx.compose.runtime.LaunchedEffect
import org.koin.androidx.compose.koinViewModel

private const val DEFAULT_SUBTYPE = "matic"

@Composable
fun VehicleDetailScreen(
    vehicleId: String? = null,
    onBack: () -> Unit,
    onManageComponents: (vehicleId: String) -> Unit = {},
    onOpenTracked: (String) -> Unit = {},
    onAddComponent: (vehicleId: String) -> Unit = {},
    onEdit: (String?) -> Unit = {},
    vm: VehicleDetailViewModel = koinViewModel(),
    componentsVm: VehicleComponentsViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val componentsState by componentsVm.state.collectAsStateWithLifecycle()
    LaunchedEffect(vehicleId) { vm.load(vehicleId) }

    val vehicle = state.vehicle
    val resolvedVehicleId = vehicle?.id
    LaunchedEffect(resolvedVehicleId) {
        resolvedVehicleId?.let { componentsVm.load(it) }
    }
    val vehicleType = vehicle?.type?.key ?: "motor"
    val subtype = vehicle?.subtypeId?.takeIf { it != "*" } ?: DEFAULT_SUBTYPE
    val subLabel = VehicleSubtypes.labelOf(vehicleType, subtype)
    val trackedItems = componentsState.items
    val tilePreview = trackedItems.take(6)
    var showShareSheet by remember { androidx.compose.runtime.mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState()),
    ) {
        VehicleDetailTopBar(
            onBack = onBack,
            onShare = { showShareSheet = true },
            onEdit = { onEdit(vehicle?.id) },
        )
        VehicleHeroCard(vehicle = vehicle)
        Spacer(Modifier.height(16.dp))
        ComponentsSectionHeader(
            total = trackedItems.size,
            subLabel = subLabel,
            onManage = { resolvedVehicleId?.let(onManageComponents) },
        )
        ComponentsGrid(
            items = tilePreview,
            vehicleType = vehicle?.type ?: VehicleType.Motor,
            onOpenTracked = onOpenTracked,
            onAddComponent = { resolvedVehicleId?.let(onAddComponent) },
        )
        Spacer(Modifier.height(16.dp))
        AdBannerSlot()
        Spacer(Modifier.height(16.dp))
        DetailSectionLabel("Servis terakhir")
        LastServicesList(records = state.recentServices)
        Spacer(Modifier.height(24.dp))
    }

    if (showShareSheet) {
        ShareVehicleSheet(
            vehicle = vehicle,
            onDismiss = { showShareSheet = false },
        )
    }
}

@Composable
private fun VehicleDetailTopBar(
    onBack: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIconButton(icon = FaIcons.CHEVRON_LEFT, onClick = onBack)
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircleIconButton(icon = FaIcons.SHARE, onClick = onShare)
            CircleIconButton(icon = FaIcons.PEN_TO_SQUARE, onClick = onEdit)
        }
    }
}

@Composable
private fun VehicleHeroCard(vehicle: Vehicle?) {
    val font = plusJakartaSansFontFamily()
    val accent = remember(vehicle?.color?.value) {
        runCatching { Color(android.graphics.Color.parseColor(vehicle?.color?.value)) }
            .getOrDefault(AppColors.Primary)
    }
    val icon = if (vehicle?.type == VehicleType.Mobil) FaIcons.CAR else FaIcons.MOTORCYCLE
    val brandLine = listOfNotNull(
        vehicle?.brand?.takeIf(String::isNotBlank)?.uppercase(),
        vehicle?.year?.toString(),
    ).joinToString(" · ").ifBlank { "—" }
    val title = vehicle?.displayTitle?.takeIf(String::isNotBlank) ?: "—"
    val plate = vehicle?.plateNumber?.takeIf(String::isNotBlank) ?: "—"
    val kmValue = vehicle?.odometer?.kilometers?.let { formatOdometerKm(it) } ?: "—"

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.13f), accent.copy(alpha = 0.05f)),
                ),
            )
            .border(BorderStroke(1.dp, accent.copy(alpha = 0.2f)), RoundedCornerShape(24.dp))
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = brandLine,
                    color = AppColors.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    fontFamily = font,
                )
                Text(
                    text = title,
                    color = AppColors.TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.4).sp,
                    fontFamily = font,
                )
                Text(
                    text = plate,
                    color = AppColors.TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(accent.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = icon, color = accent, size = 40.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(accent.copy(alpha = 0.13f)),
        )
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(label = "KM", value = kmValue, modifier = Modifier.weight(1f))
            StatTile(label = "Servis", value = "—", modifier = Modifier.weight(1f))
            StatTile(label = "Total", value = "—", modifier = Modifier.weight(1f))
        }
    }
}

private fun formatOdometerKm(km: Long): String {
    val abs = kotlin.math.abs(km).toString()
    val grouped = abs.reversed().chunked(3).joinToString(".").reversed()
    return if (km < 0) "-$grouped" else grouped
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    val font = plusJakartaSansFontFamily()
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            color = AppColors.TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            fontFamily = font,
        )
        Text(
            text = value,
            color = AppColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun DetailSectionLabel(text: String) {
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
            .padding(bottom = 6.dp),
    )
}

@Composable
private fun ComponentsSectionHeader(
    total: Int,
    subLabel: String,
    onManage: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "KOMPONEN ($total)",
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            fontFamily = font,
        )
        if (subLabel.isNotBlank()) {
            Spacer(Modifier.size(8.dp))
            SubtypeBadge(label = subLabel)
        }
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onManage)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Kelola",
                color = AppColors.Primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            FaIcon(icon = FaIcons.CHEVRON_RIGHT, color = AppColors.Primary, size = 11.sp)
        }
    }
}

@Composable
private fun ComponentsGrid(
    items: List<VehicleComponentsViewModel.TrackedItem>,
    vehicleType: VehicleType,
    onOpenTracked: (String) -> Unit,
    onAddComponent: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val rows = (items.map { it as Any? } + listOf<Any?>(null)).chunked(2)
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { item ->
                    if (item is VehicleComponentsViewModel.TrackedItem) {
                        ComponentCard(
                            item = item,
                            vehicleType = vehicleType,
                            modifier = Modifier.weight(1f),
                            onClick = { onOpenTracked(item.tracked.id) },
                        )
                    } else {
                        AddComponentCard(
                            onClick = onAddComponent,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                if (row.size < 2) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ComponentCard(
    item: VehicleComponentsViewModel.TrackedItem,
    vehicleType: VehicleType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val catalog = item.catalog
    val icon = catalog?.faIcon() ?: FaIcons.WRENCH
    val color = catalog?.uiColor() ?: AppColors.TextMuted
    val interval = item.tracked.intervalKmOverride?.let { "${it} km" }
        ?: catalog?.intervalLabelFor(vehicleType)
        ?: "—"
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(
                icon = icon,
                foreground = color,
                background = color.copy(alpha = 0.13f),
                size = 32.dp,
                iconSize = 18.sp,
                corner = 8.dp,
            )
            Spacer(Modifier.weight(1f))
            StatusDot(urgency = item.urgency.toUiUrgency())
        }
        Text(
            text = item.displayName,
            color = AppColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
        Text(
            text = interval,
            color = AppColors.TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
        )
    }
}

private fun ComponentUrgency.toUiUrgency(): ReminderUrgency = when (this) {
    ComponentUrgency.Overdue -> ReminderUrgency.Overdue
    ComponentUrgency.Soon -> ReminderUrgency.Soon
    ComponentUrgency.Ok -> ReminderUrgency.Ok
}

@Composable
private fun AddComponentCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .dashedBorder(width = 1.5.dp, color = Color(0x24141E0F), corner = 14.dp)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColors.SurfaceAlt),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = FaIcons.PLUS, color = AppColors.TextPrimary, size = 18.sp)
        }
        Text(
            text = "Tambah komponen",
            color = AppColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
        Text(
            text = "katalog / manual",
            color = AppColors.TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
        )
    }
}

@Composable
private fun LastServicesList(records: List<ServiceRecord>) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (records.isEmpty()) {
            Text(
                text = "Belum ada riwayat servis. Catat servis pertamamu lewat tombol + di bawah.",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                lineHeight = 17.sp,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        } else {
            records.forEach { record ->
                val firstComponentMeta = record.componentIds.firstOrNull()
                    ?.let { trackedComponentMeta(it) }
                val typeMeta = serviceTypeMeta(record.serviceType.key)
                val title = firstComponentMeta?.label ?: typeMeta.label.replace("\n", " ")
                val km = record.odometer.kilometers
                val date = formatServiceDate(record.serviceDate)
                val subtitle = "$date · ${"%,d".format(km).replace(',', '.')} km"
                ServiceLine(
                    icon = firstComponentMeta?.icon ?: typeMeta.icon,
                    title = title,
                    subtitle = subtitle,
                    cost = formatRupiah(record.cost.amountIdr),
                    accent = firstComponentMeta?.color ?: typeMeta.color,
                )
            }
        }
    }
}

@Composable
private fun ServiceLine(icon: String, title: String, subtitle: String, cost: String, accent: Color) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(icon = icon, foreground = accent, background = accent.copy(alpha = 0.13f))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = subtitle,
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
        }
        Text(
            text = cost,
            color = AppColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
        )
    }
}
