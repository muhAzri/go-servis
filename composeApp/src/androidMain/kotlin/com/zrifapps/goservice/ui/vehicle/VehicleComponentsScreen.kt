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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.zrifapps.goservice.feature.component.domain.model.ComponentUrgency
import com.zrifapps.goservice.feature.component.presentation.VehicleComponentsViewModel
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.EmptyState
import com.zrifapps.goservice.ui.components.FilterChipBar
import com.zrifapps.goservice.ui.components.FilterChipItem
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.Skeleton
import com.zrifapps.goservice.ui.components.SkeletonLeading
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import com.zrifapps.goservice.ui.vehicle.components.VehicleSubtypes
import com.zrifapps.goservice.ui.vehicle.components.dashedBorder
import com.zrifapps.goservice.ui.vehicle.components.faIcon
import com.zrifapps.goservice.ui.vehicle.components.intervalLabelFor
import com.zrifapps.goservice.ui.vehicle.components.uiColor
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class CompCategory(val id: String, val label: String) {
    All("all", "Semua"),
    Mesin("mesin", "Mesin"),
    Kelistrikan("kelistrikan", "Kelistrikan"),
    KakiKaki("kaki", "Kaki-kaki"),
    Pendingin("pendingin", "Pendingin"),
}

private fun categoryOf(catalogId: String): CompCategory = when (catalogId) {
    "oli_mesin", "busi", "filter_udara", "filter_oli", "tune_up", "timing_belt", "vbelt", "roller" -> CompCategory.Mesin
    "aki" -> CompCategory.Kelistrikan
    "ban", "kampas_rem", "shock", "rantai", "kampas_kopling", "oli_kopling" -> CompCategory.KakiKaki
    "radiator", "oli_gardan", "minyak_rem", "wiper", "filter_ac" -> CompCategory.Pendingin
    else -> CompCategory.Mesin
}

@Composable
fun VehicleComponentsScreen(
    vehicleId: String,
    onBack: () -> Unit,
    onOpenTracked: (String) -> Unit,
    onOpenCatalog: (String) -> Unit,
    onAdd: () -> Unit,
    vm: VehicleComponentsViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(vehicleId) { vm.load(vehicleId) }

    var activeCategory by remember { mutableStateOf("all") }

    val vehicle = state.vehicle
    val vehicleType = vehicle?.type ?: VehicleType.Motor
    val vehicleLabel = vehicle?.displayTitle ?: "—"
    val subtype = vehicle?.subtypeId?.takeIf { it.isNotBlank() && it != "*" }
        ?: VehicleSubtypes.defaultFor(vehicleType.key)
    val subLabel = VehicleSubtypes.labelOf(vehicleType.key, subtype)
    val typeLabel = if (vehicleType == VehicleType.Mobil) "Mobil" else "Motor"

    val items = state.items
    val filteredItems = remember(items, activeCategory) {
        if (activeCategory == "all") items
        else items.filter { categoryOf(it.tracked.catalogComponentId).id == activeCategory }
    }

    if (state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.BgWarm)
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            TopBar(title = "Komponen", subtitle = "Memuat…", onBack = onBack, onAdd = onAdd)
            Spacer(Modifier.height(12.dp))
            Skeleton.Tile(count = 6)
            Spacer(Modifier.height(12.dp))
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
        }
        return
    }

    if (state.isEmpty) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.BgWarm)
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            TopBar(
                title = "Komponen",
                subtitle = "$vehicleLabel · belum ada komponen",
                onBack = onBack,
                onAdd = onAdd,
            )
            EmptyState(
                modifier = Modifier.weight(1f),
                icon = FaIcons.WRENCH,
                title = "Belum ada komponen dipantau",
                body = "Pilih dari rekomendasi sesuai $subLabel — atur intervalnya, lalu pantau.",
                ctaLabel = "Pilih Komponen",
                onCta = onAdd,
            )
        }
        return
    }

    val chips = listOf(
        FilterChipItem(id = "all",         label = "Semua",       count = items.size),
        FilterChipItem(id = "mesin",       label = "Mesin",       count = items.count { categoryOf(it.tracked.catalogComponentId) == CompCategory.Mesin }),
        FilterChipItem(id = "kelistrikan", label = "Kelistrikan", count = items.count { categoryOf(it.tracked.catalogComponentId) == CompCategory.Kelistrikan }),
        FilterChipItem(id = "kaki",        label = "Kaki-kaki",   count = items.count { categoryOf(it.tracked.catalogComponentId) == CompCategory.KakiKaki }),
        FilterChipItem(id = "pendingin",   label = "Pendingin",   count = items.count { categoryOf(it.tracked.catalogComponentId) == CompCategory.Pendingin }),
    ).filter { it.id == "all" || (it.count ?: 0) > 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState()),
    ) {
        TopBar(
            title = "Komponen",
            subtitle = "$vehicleLabel · ${items.size} dipantau",
            onBack = onBack,
            onAdd = onAdd,
        )
        SubtypeBanner(
            subLabel = subLabel,
            typeLabel = typeLabel,
            vehicleType = vehicleType,
        )
        Spacer(Modifier.height(12.dp))
        FilterChipBar(
            chips = chips,
            activeId = activeCategory,
            onSelect = { activeCategory = it },
        )
        Spacer(Modifier.height(8.dp))
        TrackedComponentsList(
            items = filteredItems,
            vehicleType = vehicleType,
            onOpenTracked = onOpenTracked,
            onOpenCatalog = onOpenCatalog,
        )
        Spacer(Modifier.height(12.dp))
        AddRow(onAdd = onAdd)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TopBar(title: String, subtitle: String, onBack: () -> Unit, onAdd: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircleIconButton(icon = FaIcons.CHEVRON_LEFT, onClick = onBack)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
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
        CircleIconButton(
            icon = FaIcons.PLUS,
            onClick = onAdd,
            iconColor = AppColors.Primary,
        )
    }
}

private val accent = Color(0xFF2E8B57)

@Composable
private fun SubtypeBanner(subLabel: String, typeLabel: String, vehicleType: VehicleType) {
    val font = plusJakartaSansFontFamily()
    val icon = if (vehicleType == VehicleType.Mobil) FaIcons.CAR else FaIcons.MOTORCYCLE
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.13f), accent.copy(alpha = 0.03f)),
                ),
            )
            .border(BorderStroke(1.dp, accent.copy(alpha = 0.2f)), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = icon, color = accent, size = 22.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "SUB-TIPE",
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontFamily = font,
            )
            Text(
                text = "$subLabel · $typeLabel",
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = font,
            )
        }
    }
}

@Composable
private fun TrackedComponentsList(
    items: List<VehicleComponentsViewModel.TrackedItem>,
    vehicleType: VehicleType,
    onOpenTracked: (String) -> Unit,
    onOpenCatalog: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp)),
    ) {
        items.forEachIndexed { index, item ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(AppColors.Border),
                )
            }
            ComponentRow(
                item = item,
                vehicleType = vehicleType,
                onClick = {
                    val trackedId = item.tracked.id
                    if (trackedId.isNotBlank()) onOpenTracked(trackedId)
                    else onOpenCatalog(item.tracked.catalogComponentId)
                },
            )
        }
    }
}

@Composable
private fun ComponentRow(
    item: VehicleComponentsViewModel.TrackedItem,
    vehicleType: VehicleType,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val catalog = item.catalog
    val icon = catalog?.faIcon() ?: FaIcons.WRENCH
    val color = catalog?.uiColor() ?: AppColors.TextMuted
    val intervalLabel = item.tracked.intervalKmOverride?.let { km -> "${formatThousands(km.toInt())} km" }
        ?: catalog?.intervalLabelFor(vehicleType)
        ?: "—"
    val lastLabel = formatLastService(
        date = item.tracked.lastServiceDate,
        km = item.tracked.lastServiceOdometer?.kilometers,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(
            icon = icon,
            foreground = color,
            background = color.copy(alpha = 0.13f),
            size = 38.dp,
            iconSize = 20.sp,
            corner = 10.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = item.displayName,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
                if (item.urgency == ComponentUrgency.Overdue) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(AppColors.Danger))
                }
            }
            Text(
                text = "Terakhir: $lastLabel",
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
            formatNextService(
                date = item.tracked.nextServiceDate,
                km = item.tracked.nextServiceOdometer?.kilometers,
            )?.let { nextLabel ->
                Text(
                    text = "Berikutnya: $nextLabel",
                    color = when (item.urgency) {
                        ComponentUrgency.Overdue -> AppColors.Danger
                        ComponentUrgency.Soon -> AppColors.Primary
                        ComponentUrgency.Ok -> AppColors.TextMuted
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "INTERVAL",
                color = AppColors.TextSubtle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontFamily = font,
            )
            Text(
                text = intervalLabel,
                color = AppColors.TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
            )
        }
        Spacer(Modifier.size(4.dp))
        FaIcon(icon = FaIcons.CHEVRON_RIGHT, color = AppColors.TextSubtle, size = 12.sp)
    }
}

@Composable
private fun AddRow(onAdd: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .dashedBorder(width = 1.5.dp, color = Color(0x24141E0F), corner = 16.dp)
            .clickable(onClick = onAdd)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppColors.SurfaceAlt),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = FaIcons.PLUS, color = AppColors.TextPrimary, size = 18.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Tambah komponen",
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = "Dari rekomendasi atau ketik sendiri",
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
        }
        FaIcon(icon = FaIcons.CHEVRON_RIGHT, color = AppColors.TextSubtle, size = 14.sp)
    }
}

private val dateFormat: SimpleDateFormat by lazy {
    SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID"))
}

private fun formatLastService(date: Long?, km: Long?): String {
    if (date == null && km == null) return "belum tercatat"
    val parts = mutableListOf<String>()
    km?.let { parts.add("${formatThousands(it.toInt())} km") }
    date?.let { parts.add(dateFormat.format(Date(it))) }
    return parts.joinToString(" · ")
}

private fun formatNextService(date: Long?, km: Long?): String? {
    if (date == null && km == null) return null
    val parts = mutableListOf<String>()
    km?.let { parts.add("${formatThousands(it.toInt())} km") }
    date?.let { parts.add(dateFormat.format(Date(it))) }
    return parts.joinToString(" · ")
}

private fun formatThousands(value: Int): String {
    if (value == 0) return "0"
    val abs = kotlin.math.abs(value).toString()
    val grouped = abs.reversed().chunked(3).joinToString(".").reversed()
    return if (value < 0) "-$grouped" else grouped
}
