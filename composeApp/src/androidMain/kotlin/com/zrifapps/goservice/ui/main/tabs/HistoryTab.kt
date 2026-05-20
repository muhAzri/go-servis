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
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.NativeAdCard
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun HistoryTab(
    modifier: Modifier = Modifier,
    onOpenServiceDetail: () -> Unit = {},
) {
    var selectedFilter by remember { mutableStateOf("Semua kendaraan") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabHeader(
            subtitle = "5 servis tercatat · Total Rp 3.310.000",
            title = "Riwayat Servis",
        )

        VehicleFilterScroller(
            selected = selectedFilter,
            onSelect = { selectedFilter = it },
        )
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
