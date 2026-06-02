package com.zrifapps.goservice.ui.main.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceSort
import com.zrifapps.goservice.feature.service.presentation.ServiceHistoryViewModel
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleListViewModel
import com.zrifapps.goservice.ui.components.EmptyState
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.NativeAdCard
import com.zrifapps.goservice.ui.components.Skeleton
import com.zrifapps.goservice.ui.components.SkeletonLeading
import com.zrifapps.goservice.ui.components.SortChip
import com.zrifapps.goservice.ui.components.StickySearchHeader
import com.zrifapps.goservice.ui.components.PullRefreshIndicator
import com.zrifapps.goservice.ui.components.PullRefreshState
import com.zrifapps.goservice.ui.main.sheets.HistoryFilterSheet
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel

private val sortOptions = listOf(
    "date_desc" to "Terbaru dulu",
    "date_asc" to "Terlama dulu",
    "cost_desc" to "Termahal dulu",
    "cost_asc" to "Termurah dulu",
    "vehicle" to "Per kendaraan",
)

@Composable
fun HistoryTab(
    modifier: Modifier = Modifier,
    onOpenServiceDetail: (String) -> Unit = {},
    onAddService: () -> Unit = {},
    isRefreshing: Boolean = false,
    serviceVm: ServiceHistoryViewModel = koinViewModel(),
    vehicleVm: VehicleListViewModel = koinViewModel(),
) {
    val serviceState by serviceVm.state.collectAsStateWithLifecycle()
    val vehicleState by vehicleVm.state.collectAsStateWithLifecycle()
    val isLoading = serviceState.isLoading
    val isEmpty = serviceState.isEmpty
    val vehicleById = vehicleState.vehicles.associateBy { it.id }
    val filterOptions = remember(vehicleState.vehicles) {
        listOf("Semua kendaraan") + vehicleState.vehicles.map { it.displayTitle }
    }
    val selectedFilter = serviceState.vehicleIds
        .firstOrNull()
        ?.let { id -> vehicleById[id]?.displayTitle }
        ?: "Semua kendaraan"
    val searchQuery = serviceState.query
    val sortKey = serviceState.sort.toKey()
    val sortLabel = sortOptions.firstOrNull { it.first == sortKey }?.second ?: "Terbaru"

    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    if (isLoading) {
        Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            TabHeader(subtitle = null, title = "Riwayat Servis")
            Spacer(Modifier.height(12.dp))
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
        }
        return
    }

    if (isEmpty) {
        Column(modifier = modifier.fillMaxSize()) {
            TabHeader(subtitle = "Belum ada catatan", title = "Riwayat Servis")
            EmptyState(
                modifier = Modifier.weight(1f),
                icon = FaIcons.WRENCH,
                title = "Belum ada servis tercatat",
                body = "Catat servis pertama untuk mulai melacak biaya & interval per komponen.",
                ctaLabel = "Catat Servis",
                onCta = onAddService,
            )
        }
        return
    }

    val sortedRecords = serviceState.records
    val grouped = sortedRecords.groupBy { yearMonthLabel(it.serviceDate) }
    val visibleCount = serviceState.totalCount
    val totalCost = serviceState.totalCostIdr

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabHeader(
            subtitle = "$visibleCount servis tercatat · Total ${formatRupiah(totalCost)}",
            title = "Riwayat Servis",
        )

        StickySearchHeader(
            value = searchQuery,
            onValueChange = serviceVm::setQuery,
            placeholder = "Cari servis, bengkel, kendaraan…",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SortChip(label = sortLabel, onClick = { showSortSheet = true })
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AppColors.Surface)
                    .border(BorderStroke(1.dp, AppColors.Border), CircleShape)
                    .clickable { showFilterSheet = true }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FaIcon(icon = FaIcons.FILTER, color = AppColors.TextMuted, size = 12.sp)
                    Text(
                        text = "Filter",
                        color = AppColors.TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = plusJakartaSansFontFamily(),
                    )
                }
            }
        }
        VehicleFilterScroller(
            options = filterOptions,
            selected = selectedFilter,
            onSelect = { label ->
                val ids = if (label == "Semua kendaraan") emptySet()
                else vehicleState.vehicles.firstOrNull { it.displayTitle == label }
                    ?.let { setOf(it.id) } ?: emptySet()
                serviceVm.setVehicleIds(ids)
            },
        )
        if (isRefreshing) {
            PullRefreshIndicator(state = PullRefreshState.Refreshing)
        }
        Spacer(Modifier.height(14.dp))

        grouped.entries.forEachIndexed { idx, (label, records) ->
            MonthSeparator(label)
            records.forEach { record ->
                HistoryRowCard(
                    record = record,
                    vehicle = vehicleById[record.vehicleId],
                    onClick = { onOpenServiceDetail(record.id) },
                )
            }
            if (idx == 2) {
                Spacer(Modifier.height(4.dp))
                NativeAdCard()
                Spacer(Modifier.height(4.dp))
            }
        }
        if (serviceState.canLoadMore) {
            LoadMoreButton(
                shownCount = sortedRecords.size,
                totalCount = visibleCount,
                onClick = serviceVm::loadMore,
            )
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showSortSheet) {
        SortOptionsSheet(
            options = sortOptions,
            selectedKey = sortKey,
            onSelect = { key -> serviceVm.setSort(key.toServiceSort()); showSortSheet = false },
            onDismiss = { showSortSheet = false },
        )
    }
    if (showFilterSheet) {
        HistoryFilterSheet(
            onDismiss = { showFilterSheet = false },
            onApply = { _ -> showFilterSheet = false },
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun SortOptionsSheet(
    options: List<Pair<String, String>>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val font = plusJakartaSansFontFamily()
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "Urutkan riwayat",
                color = AppColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = font,
                modifier = Modifier.padding(bottom = 14.dp),
            )
            options.forEach { (key, label) ->
                val active = key == selectedKey
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (active) AppColors.PrimarySoft else AppColors.SurfaceAlt)
                        .clickable { onSelect(key) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (active) AppColors.Primary else Color.Transparent)
                            .border(BorderStroke(2.dp, if (active) AppColors.Primary else AppColors.Border), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (active) Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                    }
                    Text(
                        text = label,
                        color = AppColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = font,
                    )
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun VehicleFilterScroller(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    val font = plusJakartaSansFontFamily()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { opt ->
            val active = opt == selected
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (active) AppColors.Primary else AppColors.Surface)
                    .then(
                        if (active) Modifier
                        else Modifier.border(BorderStroke(1.dp, AppColors.Border), CircleShape)
                    )
                    .clickable { onSelect(opt) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = opt,
                    color = if (active) Color.White else AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}

@Composable
private fun MonthSeparator(label: String) {
    val font = plusJakartaSansFontFamily()
    Text(
        text = label.uppercase(),
        color = AppColors.TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        fontFamily = font,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 8.dp),
    )
}

@Composable
private fun HistoryRowCard(
    record: ServiceRecord,
    vehicle: Vehicle?,
    onClick: () -> Unit = {},
) {
    val font = plusJakartaSansFontFamily()
    val icon = serviceIcon(record)
    val accent = AppColors.Primary
    val title = record.serviceType.name
    val vehicleLabel = vehicle?.displayTitle ?: "—"
    val km = "${formatGroupedLong(record.odometer.kilometers)} km"
    val place = record.workshop?.ifBlank { null } ?: "—"
    val note = record.note.orEmpty()
    val cost = formatRupiah(record.cost.amountIdr)
    val date = formatShortDate(record.serviceDate)
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(icon = icon, foreground = accent, background = accent.copy(alpha = 0.13f))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = cost,
                    color = AppColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Text(
                text = "$vehicle · $km",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontFamily = font,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 2.dp),
            ) {
                FaIcon(icon = FaIcons.LOCATION_DOT, color = AppColors.TextSubtle, size = 10.sp)
                Text(
                    text = place,
                    color = AppColors.TextSubtle,
                    fontSize = 12.sp,
                    fontFamily = font,
                )
            }
            if (note.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.SurfaceAlt)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = note,
                        color = AppColors.TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
            Text(
                text = date,
                color = AppColors.TextSubtle,
                fontSize = 11.sp,
                fontFamily = font,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private val monthNames = arrayOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
)

private fun yearMonthLabel(epochMillis: Long): String {
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${monthNames[dt.month.number - 1]} ${dt.year}"
}

private fun formatShortDate(epochMillis: Long): String {
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    val month = monthNames[dt.month.number - 1].take(3)
    return "${dt.day} $month ${dt.year}"
}

private fun formatGroupedLong(value: Long): String {
    if (value == 0L) return "0"
    val parts = mutableListOf<String>()
    var n = value
    while (n > 0) {
        val chunk = n % 1000
        n /= 1000
        if (n > 0) parts.add(0, chunk.toString().padStart(3, '0'))
        else parts.add(0, chunk.toString())
    }
    return parts.joinToString(".")
}

private fun formatRupiah(amountIdr: Long): String = "Rp ${formatGroupedLong(amountIdr)}"

private fun String.toServiceSort(): ServiceSort = when (this) {
    "date_asc" -> ServiceSort.DateAsc
    "cost_desc" -> ServiceSort.CostDesc
    "cost_asc" -> ServiceSort.CostAsc
    "vehicle" -> ServiceSort.OdometerAsc
    else -> ServiceSort.DateDesc
}

private fun ServiceSort.toKey(): String = when (this) {
    ServiceSort.DateDesc -> "date_desc"
    ServiceSort.DateAsc -> "date_asc"
    ServiceSort.CostDesc -> "cost_desc"
    ServiceSort.CostAsc -> "cost_asc"
    ServiceSort.OdometerDesc, ServiceSort.OdometerAsc -> "vehicle"
}

@Composable
private fun LoadMoreButton(shownCount: Int, totalCount: Int, onClick: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(AppColors.Surface)
                .border(BorderStroke(1.dp, AppColors.Border), CircleShape)
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Muat lebih banyak ($shownCount/$totalCount)",
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            FaIcon(icon = FaIcons.CHEVRON_DOWN, color = AppColors.TextMuted, size = 12.sp)
        }
    }
}

private fun serviceIcon(record: ServiceRecord): String = when (record.serviceType.key) {
    "oli" -> FaIcons.OIL_CAN
    "filter" -> FaIcons.FILTER
    "ban" -> FaIcons.LIFE_RING
    "aki" -> FaIcons.CAR_BATTERY
    "rem" -> FaIcons.CIRCLE_NOTCH
    "tune_up" -> FaIcons.BOLT
    else -> FaIcons.WRENCH
}
