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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.ContextBanner
import com.zrifapps.goservice.ui.components.ContextBannerTone
import com.zrifapps.goservice.ui.components.DetailToolbar
import com.zrifapps.goservice.ui.components.DetailToolbarAction
import com.zrifapps.goservice.ui.components.ShareCardData
import com.zrifapps.goservice.ui.components.ShareCardPoster
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

private enum class ShareFormat { Image, Text }
private enum class ShareSize(val label: String, val sub: String, val symbol: String) {
    Square("1:1", "1080×1080", "◼"),
    Portrait("4:5", "1080×1350", "▮"),
    Story("9:16", "1080×1920", "▯"),
}

private data class IncludeOption(val id: String, val label: String, val initiallyOn: Boolean)

private val defaultIncludes = listOf(
    IncludeOption("vehicle", "Nama & plat kendaraan", true),
    IncludeOption("cost", "Biaya servis", true),
    IncludeOption("workshop", "Nama bengkel", true),
    IncludeOption("note", "Catatan teknis", false),
)

private val previewData = ShareCardData(
    serviceLabel = "Ganti Oli Mesin",
    serviceIcon = FaIcons.OIL_CAN,
    vehicleName = "Beat Hitam",
    vehiclePlate = "B 4521 KZA",
    vehicleSpec = "Honda BeAT 110",
    vehicleIcon = FaIcons.MOTORCYCLE,
    dateLabel = "20 Feb 2026",
    kmLabel = "16.000 km",
    costLabel = "Rp 65.000",
    workshopLabel = "AHASS Kebon Jeruk",
)

@Composable
fun ShareImageCardScreen(
    onBack: () -> Unit,
    onShare: () -> Unit = {},
    onSave: () -> Unit = {},
    data: ShareCardData = previewData,
) {
    val font = plusJakartaSansFontFamily()
    var format by remember { mutableStateOf(ShareFormat.Image) }
    var sizeChoice by remember { mutableStateOf(ShareSize.Portrait) }
    var includes by remember { mutableStateOf(defaultIncludes.map { it.id to it.initiallyOn }.toMap()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        DetailToolbar(
            title = "Bagikan Servis",
            onBack = onBack,
            actions = emptyList<DetailToolbarAction>(),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                ShareCardPoster(data = data)
            }

            SectionLabel("Format")
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.SurfaceAlt)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ShareFormat.entries.forEach { f ->
                    val active = format == f
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (active) AppColors.Surface else Color.Transparent)
                            .clickable { format = f }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (f == ShareFormat.Image) "Gambar (PNG)" else "Teks",
                            color = if (active) AppColors.TextPrimary else AppColors.TextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font,
                        )
                    }
                }
            }
            Spacer(Modifier.height(18.dp))

            SectionLabel("Ukuran")
            Row(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ShareSize.entries.forEach { s ->
                    val active = sizeChoice == s
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (active) AppColors.PrimarySoft else AppColors.Surface)
                            .border(
                                BorderStroke(1.5.dp, if (active) AppColors.Primary else AppColors.Border),
                                RoundedCornerShape(14.dp),
                            )
                            .clickable { sizeChoice = s }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = s.symbol,
                            color = if (active) AppColors.Primary else AppColors.TextMuted,
                            fontSize = 18.sp,
                            fontFamily = font,
                        )
                        Text(
                            text = s.label,
                            color = if (active) AppColors.Primary else AppColors.TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = font,
                        )
                        Text(
                            text = s.sub,
                            color = AppColors.TextSubtle,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
            Spacer(Modifier.height(18.dp))

            SectionLabel("Sertakan")
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Surface)
                    .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(14.dp)),
            ) {
                defaultIncludes.forEachIndexed { idx, opt ->
                    val on = includes[opt.id] == true
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includes = includes + (opt.id to !on) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = opt.label,
                            color = AppColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = font,
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(22.dp)
                                .clip(CircleShape)
                                .background(if (on) AppColors.Primary else AppColors.SurfaceAlt)
                                .padding(2.dp),
                            contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                            )
                        }
                    }
                    if (idx < defaultIncludes.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .height(1.dp)
                                .background(AppColors.Border),
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                ContextBanner(
                    title = "Cocok untuk warranty claim",
                    body = "Bagikan ke grup dealer atau simpan sebagai bukti servis. ID #${data.recordId.uppercase()} unik per record.",
                    icon = FaIcons.CIRCLE_INFO,
                    tone = ContextBannerTone.Info,
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        BottomActionBar(onSave = onSave, onShare = onShare)
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
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun BottomActionBar(onSave: () -> Unit, onShare: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(0.dp))
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 18.dp)),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
                .clickable(onClick = onSave)
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FaIcon(icon = FaIcons.FILE, color = AppColors.TextPrimary, size = 14.sp)
            Text(
                text = "Simpan",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Primary)
                .clickable(onClick = onShare),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            FaIcon(icon = FaIcons.SHARE, color = Color.White, size = 14.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Bagikan Gambar",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }
}
