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
    onOpenServiceDetail: () -> Unit = {},
    onAddService: () -> Unit = {},
    isEmpty: Boolean = false,
    isLoading: Boolean = false,
    isRefreshing: Boolean = false,
) {
    var selectedFilter by remember { mutableStateOf("Semua kendaraan") }
    var searchQuery by remember { mutableStateOf("") }
    var sortKey by remember { mutableStateOf("date_desc") }
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val sortLabel = sortOptions.firstOrNull { it.first == sortKey }?.second ?: "Terbaru"

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabHeader(
            subtitle = "5 servis tercatat · Total Rp 3.310.000",
            title = "Riwayat Servis",
        )

        StickySearchHeader(
            value = searchQuery,
            onValueChange = { searchQuery = it },
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
            selected = selectedFilter,
            onSelect = { selectedFilter = it },
        )
        if (isRefreshing) {
            PullRefreshIndicator(state = PullRefreshState.Refreshing)
        }
        Spacer(Modifier.height(14.dp))

        MonthSeparator("Mei 2026")
        HistoryRowCard(
            icon = FaIcons.WRENCH,
            accent = Color(0xFF5C6357),
            title = "Servis Berkala",
            vehicle = "Vario Merah",
            km = "6.000 km",
            place = "AHASS Kalimalang",
            note = "KPB ke-2",
            cost = "Rp 320.000",
            date = "1 Mei 2026",
            onClick = onOpenServiceDetail,
        )

        MonthSeparator("Maret 2026")
        HistoryRowCard(
            icon = FaIcons.OIL_CAN,
            accent = Color(0xFFE89C2E),
            title = "Ganti Oli Mesin",
            vehicle = "Beat Hitam",
            km = "16.000 km",
            place = "AHASS Kebon Jeruk",
            note = "AHM MPX2 0.8L",
            cost = "Rp 65.000",
            date = "20 Feb 2026",
            onClick = onOpenServiceDetail,
        )

        MonthSeparator("Februari 2026")
        HistoryRowCard(
            icon = FaIcons.OIL_CAN,
            accent = Color(0xFFE89C2E),
            title = "Ganti Oli Mesin",
            vehicle = "Beat Hitam",
            km = "16.000 km",
            place = "AHASS Kebon Jeruk",
            note = "AHM MPX2 0.8L",
            cost = "Rp 65.000",
            date = "20 Feb 2026",
            onClick = onOpenServiceDetail,
        )

        Spacer(Modifier.height(4.dp))
        NativeAdCard()
        Spacer(Modifier.height(4.dp))

        MonthSeparator("Januari 2026")
        HistoryRowCard(
            icon = FaIcons.OIL_CAN,
            accent = Color(0xFFE89C2E),
            title = "Ganti Oli Mesin",
            vehicle = "Avanza Putih",
            km = "57.500 km",
            place = "Auto2000 Cikarang",
            note = "Motul 5W-30 4L + filter",
            cost = "Rp 480.000",
            date = "5 Jan 2026",
            onClick = onOpenServiceDetail,
        )

        MonthSeparator("Desember 2025")
        HistoryRowCard(
            icon = FaIcons.BOLT,
            accent = Color(0xFFE8B62E),
            title = "Busi & Tune Up",
            vehicle = "Beat Hitam",
            km = "13.800 km",
            place = "Bengkel Pak Karto",
            note = "NGK CPR8EA",
            cost = "Rp 45.000",
            date = "10 Des 2025",
            onClick = onOpenServiceDetail,
        )

        MonthSeparator("September 2025")
        HistoryRowCard(
            icon = FaIcons.LIFE_RING,
            accent = Color(0xFF3F4D5C),
            title = "Rotasi/Ganti Ban",
            vehicle = "Avanza Putih",
            km = "50.000 km",
            place = "Bridgestone Bekasi",
            note = "Turanza 185/65 R15 4 pcs",
            cost = "Rp 2.400.000",
            date = "22 Sep 2025",
            onClick = onOpenServiceDetail,
        )
        Spacer(Modifier.height(24.dp))
    }

    if (showSortSheet) {
        SortOptionsSheet(
            options = sortOptions,
            selectedKey = sortKey,
            onSelect = { sortKey = it; showSortSheet = false },
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
private fun VehicleFilterScroller(selected: String, onSelect: (String) -> Unit) {
    val font = plusJakartaSansFontFamily()
    val options = listOf("Semua kendaraan", "Beat Hitam", "Vario Merah", "Avanza Putih", "Brio Biru")

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
    icon: String,
    accent: Color,
    title: String,
    vehicle: String,
    km: String,
    place: String,
    note: String,
    cost: String,
    date: String,
    onClick: () -> Unit = {},
) {
    val font = plusJakartaSansFontFamily()
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
