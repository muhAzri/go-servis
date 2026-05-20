package com.zrifapps.goservice.ui.reminders

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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
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
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.ContextBanner
import com.zrifapps.goservice.ui.components.ContextBannerTone
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

private data class ServiceChoice(
    val id: String,
    val label: String,
    val icon: String,
    val color: Color,
)

private val serviceChoices = listOf(
    ServiceChoice("oli",      "Ganti Oli",  FaIcons.OIL_CAN,         Color(0xFFE89C2E)),
    ServiceChoice("filter",   "Filter",     FaIcons.FILTER,          Color(0xFF7B6FE8)),
    ServiceChoice("rem",      "Kampas Rem", FaIcons.CIRCLE_NOTCH,    Color(0xFF2E8B57)),
    ServiceChoice("ban",      "Ban",        FaIcons.LIFE_RING,       Color(0xFF3F4D5C)),
    ServiceChoice("aki",      "Aki",        FaIcons.CAR_BATTERY,     Color(0xFFD6453A)),
    ServiceChoice("radiator", "Coolant",    FaIcons.TEMPERATURE_HALF,Color(0xFF3FB1D6)),
)

enum class TriggerMode { Km, Date, Both }

@Composable
fun AddReminderScreen(
    onClose: () -> Unit,
    onSaved: () -> Unit = {},
    onOpenNotifSettings: () -> Unit = {},
    fromContext: Boolean = false,
    notifPermissionGranted: Boolean = true,
) {
    var selectedService by remember { mutableStateOf("oli") }
    var trigger by remember { mutableStateOf(TriggerMode.Km) }
    var targetKm by remember { mutableStateOf("20.420") }
    var targetDate by remember { mutableStateOf("6 Jul 2026") }
    var note by remember { mutableStateOf("") }
    var showCtxBanner by remember { mutableStateOf(fromContext) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        TopBar(onClose = onClose, onSave = onSaved)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            if (showCtxBanner) {
                ContextBanner(
                    title = "Dari servis tercatat",
                    body = "Ganti Oli Mesin · Beat Hitam — field di bawah sudah diisi otomatis.",
                    icon = FaIcons.BELL,
                    tone = ContextBannerTone.Info,
                    onDismiss = { showCtxBanner = false },
                )
                Spacer(Modifier.height(16.dp))
            }

            FieldLabel("Kendaraan")
            VehiclePickerRow(locked = showCtxBanner)
            Spacer(Modifier.height(18.dp))

            FieldLabel("Jenis servis")
            ServiceTypeGrid(
                selected = selectedService,
                onSelect = { selectedService = it },
                locked = showCtxBanner,
            )
            Spacer(Modifier.height(18.dp))

            FieldLabel("Picu pengingat")
            TriggerSegmented(selected = trigger, onSelect = { trigger = it })
            Spacer(Modifier.height(14.dp))

            if (trigger == TriggerMode.Km || trigger == TriggerMode.Both) {
                NumberField(
                    label = "Target KM",
                    value = targetKm,
                    onValueChange = { targetKm = it },
                    helper = "Interval pabrikan: 2.000 km · KM saat ini 18.420",
                )
            }
            if (trigger == TriggerMode.Date || trigger == TriggerMode.Both) {
                Spacer(Modifier.height(12.dp))
                DateField(
                    label = "Tanggal",
                    value = targetDate,
                    onTap = {},
                    helper = "Pengingat dimulai: 7 hari sebelum",
                )
            }
            Spacer(Modifier.height(18.dp))

            FieldLabel("Catatan (opsional)")
            NoteField(value = note, onValueChange = { note = it })
            Spacer(Modifier.height(18.dp))

            if (!notifPermissionGranted) {
                ContextBanner(
                    title = "Notif belum aktif",
                    body = "Pengingat servis tidak akan muncul di lock screen. Aktifkan supaya tidak kelewat.",
                    icon = FaIcons.BELL_SLASH,
                    tone = ContextBannerTone.Warning,
                    ctaLabel = "Aktifkan →",
                    onCta = onOpenNotifSettings,
                )
                Spacer(Modifier.height(20.dp))
            }
        }

        BottomSaveBar(onSave = onSaved)
    }
}

@Composable
private fun TopBar(onClose: () -> Unit, onSave: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIconButton(icon = FaIcons.XMARK, onClick = onClose)
        Spacer(Modifier.width(12.dp))
        Text(
            text = "Buat Pengingat",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "Simpan",
            color = AppColors.Primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onSave)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    val font = plusJakartaSansFontFamily()
    Text(
        text = text.uppercase(),
        color = AppColors.TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        fontFamily = font,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun VehiclePickerRow(locked: Boolean) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (locked) AppColors.SurfaceAlt else AppColors.Surface)
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
        FaIcon(
            icon = if (locked) FaIcons.LOCK else FaIcons.CHEVRON_DOWN,
            color = AppColors.TextSubtle,
            size = 14.sp,
        )
    }
}

@Composable
private fun ServiceTypeGrid(
    selected: String,
    onSelect: (String) -> Unit,
    locked: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        serviceChoices.chunked(3).forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                chunk.forEach { c ->
                    ServiceTile(
                        choice = c,
                        active = selected == c.id,
                        enabled = !locked,
                        onClick = { if (!locked) onSelect(c.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (chunk.size < 3) repeat(3 - chunk.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ServiceTile(
    choice: ServiceChoice,
    active: Boolean,
    enabled: Boolean,
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
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp),
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
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
        )
    }
}

@Composable
private fun TriggerSegmented(selected: TriggerMode, onSelect: (TriggerMode) -> Unit) {
    val font = plusJakartaSansFontFamily()
    val options = listOf(
        TriggerMode.Km to "Per KM",
        TriggerMode.Date to "Per Tanggal",
        TriggerMode.Both to "Keduanya",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.SurfaceAlt)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { (mode, label) ->
            val active = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (active) AppColors.Surface else Color.Transparent)
                    .clickable { onSelect(mode) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (active) AppColors.TextPrimary else AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}

@Composable
private fun NumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    helper: String,
) {
    val font = plusJakartaSansFontFamily()
    Column {
        FieldLabel(label)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FaIcon(icon = FaIcons.GAUGE, color = AppColors.TextMuted, size = 16.sp)
            Text(
                text = value,
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "km",
                color = AppColors.TextSubtle,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
            )
        }
        Text(
            text = helper,
            color = AppColors.TextSubtle,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            modifier = Modifier.padding(top = 6.dp, start = 4.dp),
        )
    }
}

@Composable
private fun DateField(label: String, value: String, onTap: () -> Unit, helper: String) {
    val font = plusJakartaSansFontFamily()
    Column {
        FieldLabel(label)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
                .clickable(onClick = onTap)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FaIcon(icon = FaIcons.CALENDAR, color = AppColors.TextMuted, size = 16.sp)
            Text(
                text = value,
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
                modifier = Modifier.weight(1f),
            )
            FaIcon(icon = FaIcons.CHEVRON_DOWN, color = AppColors.TextSubtle, size = 12.sp)
        }
        Text(
            text = helper,
            color = AppColors.TextSubtle,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            modifier = Modifier.padding(top = 6.dp, start = 4.dp),
        )
    }
}

@Composable
private fun NoteField(value: String, onValueChange: (String) -> Unit) {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = if (value.isEmpty()) "Tambah catatan…" else value,
            color = if (value.isEmpty()) AppColors.TextSubtle else AppColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun BottomSaveBar(onSave: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.Primary)
                .clickable(onClick = onSave),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Simpan Pengingat",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }
}
