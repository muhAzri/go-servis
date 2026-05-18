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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
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
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

private data class ServiceTypeChoice(
    val id: String,
    val label: String,
    val icon: String,
    val color: Color,
)

private val serviceTypes = listOf(
    ServiceTypeChoice("oli",      "Ganti Oli\nMesin",    FaIcons.OIL_CAN,         Color(0xFFE89C2E)),
    ServiceTypeChoice("filter",   "Filter Oli\n& Udara", FaIcons.FILTER,          Color(0xFF7B6FE8)),
    ServiceTypeChoice("ban",      "Rotasi/\nGanti Ban",  FaIcons.LIFE_RING,       Color(0xFF3F4D5C)),
    ServiceTypeChoice("aki",      "Aki",                  FaIcons.CAR_BATTERY,     Color(0xFFD6453A)),
    ServiceTypeChoice("rem",      "Kampas\nRem",         FaIcons.CIRCLE_NOTCH,    Color(0xFF2E8B57)),
    ServiceTypeChoice("radiator", "Radiator/\nCoolant",  FaIcons.TEMPERATURE_HALF,Color(0xFF3FB1D6)),
)

@Composable
fun AddServiceScreen(
    onClose: () -> Unit,
    onSaved: () -> Unit = {},
) {
    var selected by remember { mutableStateOf("oli") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        TopBar(onClose = onClose)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(vertical = 8.dp),
        ) {
            FieldLabel("Kendaraan")
            VehiclePickerRow()
            Spacer(Modifier.height(18.dp))

            FieldLabel("Jenis servis")
            ServiceTypeGrid(selected = selected, onSelect = { selected = it })
            Spacer(Modifier.height(18.dp))

            PlainField(label = "Tanggal servis", value = "6 Mei 2026", icon = FaIcons.CALENDAR)
            PlainField(label = "KM saat servis", value = "18.420", icon = FaIcons.GAUGE, monospaced = true)
            PlainField(label = "Bengkel", value = "AHASS Kebon Jeruk", icon = FaIcons.LOCATION_DOT)
            PlainField(label = "Biaya", value = "Rp 65.000", icon = null, monospaced = true)
            PlainField(label = "Catatan", value = "AHM MPX2 0.8L", icon = null, tall = true)

            Spacer(Modifier.height(4.dp))
            AutoReminderInfoCard()
            Spacer(Modifier.height(20.dp))
        }

        SaveBar(onSave = onSaved)
    }
}

@Composable
private fun TopBar(onClose: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircleIconButton(icon = FaIcons.XMARK, onClick = onClose)
        Text(
            text = "Catat Servis",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    val font = plusJakartaSansFontFamily()
    Text(
        text = text,
        color = AppColors.TextMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = font,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun VehiclePickerRow() {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(
            icon = FaIcons.MOTORCYCLE,
            foreground = AppColors.Primary,
            background = AppColors.PrimarySoft,
            size = 40.dp, iconSize = 22.sp, corner = 10.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Beat Hitam",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = "B 4521 KZA",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
            )
        }
        FaIcon(icon = FaIcons.CHEVRON_DOWN, color = AppColors.TextSubtle, size = 14.sp)
    }
}

@Composable
private fun ServiceTypeGrid(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        serviceTypes.chunked(3).forEach { rowChoices ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowChoices.forEach { choice ->
                    ServiceTypeTile(
                        choice = choice,
                        active = choice.id == selected,
                        onClick = { onSelect(choice.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowChoices.size < 3) {
                    repeat(3 - rowChoices.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceTypeTile(
    choice: ServiceTypeChoice,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) AppColors.PrimarySoft else AppColors.Surface)
            .border(
                BorderStroke(
                    1.5.dp,
                    if (active) AppColors.Primary else AppColors.Border,
                ),
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FaIcon(
            icon = choice.icon,
            color = if (active) AppColors.Primary else choice.color,
            size = 22.sp,
        )
        Text(
            text = choice.label,
            color = AppColors.TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 12.sp,
        )
    }
}

@Composable
private fun PlainField(
    label: String,
    value: String,
    icon: String?,
    monospaced: Boolean = false,
    tall: Boolean = false,
) {
    val font = plusJakartaSansFontFamily()
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = if (tall) 18.dp else 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (icon != null) {
                FaIcon(icon = icon, color = AppColors.TextMuted, size = 16.sp)
            }
            Text(
                text = value,
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = if (monospaced) FontFamily.Monospace else font,
            )
        }
    }
}

@Composable
private fun AutoReminderInfoCard() {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.PrimarySofter)
            .border(BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.19f)), RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FaIcon(icon = FaIcons.CIRCLE_INFO, color = AppColors.Primary, size = 14.sp)
            Text(
                text = "Pengingat berikutnya akan diset otomatis",
                color = AppColors.Primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
        Text(
            text = "Berdasarkan interval pabrikan: target ganti oli berikutnya 20.420 km atau 6 Juli 2026.",
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            lineHeight = 17.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun SaveBar(onSave: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp)),
    ) {
        AppButton(text = "Simpan Servis", onClick = onSave)
    }
}
