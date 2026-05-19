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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.ReminderUrgency
import com.zrifapps.goservice.ui.components.StatusDot
import com.zrifapps.goservice.ui.components.color
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import com.zrifapps.goservice.ui.vehicle.components.ComponentInfo
import com.zrifapps.goservice.ui.vehicle.components.ComponentsCatalog
import com.zrifapps.goservice.ui.vehicle.components.VehicleSubtypes
import com.zrifapps.goservice.ui.vehicle.components.dashedBorder

private const val DEFAULT_VEHICLE_TYPE = "motor"
private const val DEFAULT_SUBTYPE = "matic"
private const val VEHICLE_NAME = "Beat Hitam"
private val accent = Color(0xFF2E8B57)

@Composable
fun VehicleComponentsScreen(
    onBack: () -> Unit,
    onOpenComponent: (String) -> Unit,
    onAdd: () -> Unit,
    onChangeSubtype: () -> Unit = {},
    vehicleType: String = DEFAULT_VEHICLE_TYPE,
    subtype: String = DEFAULT_SUBTYPE,
) {
    val components = ComponentsCatalog.forSubtype(subtype)
    val subLabel = VehicleSubtypes.labelOf(vehicleType, subtype)
    val typeLabel = if (vehicleType == "mobil") "Mobil" else "Motor"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState()),
    ) {
        TopBar(
            title = "Komponen",
            subtitle = "$VEHICLE_NAME · ${components.size} dipantau",
            onBack = onBack,
            onAdd = onAdd,
        )
        SubtypeBanner(
            subLabel = subLabel,
            typeLabel = typeLabel,
            onChange = onChangeSubtype,
        )
        Spacer(Modifier.height(12.dp))
        ComponentsList(
            components = components,
            vehicleType = vehicleType,
            onOpenComponent = onOpenComponent,
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
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.Primary)
                .clickable(onClick = onAdd),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = FaIcons.PLUS, color = Color.White, size = 16.sp)
        }
    }
}

@Composable
private fun SubtypeBanner(subLabel: String, typeLabel: String, onChange: () -> Unit) {
    val font = plusJakartaSansFontFamily()
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
            FaIcon(icon = FaIcons.MOTORCYCLE, color = accent, size = 22.sp)
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
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(AppColors.Surface)
                .border(BorderStroke(1.dp, AppColors.Border), CircleShape)
                .clickable(onClick = onChange)
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Ubah",
                color = AppColors.TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }
}

@Composable
private fun ComponentsList(
    components: List<ComponentInfo>,
    vehicleType: String,
    onOpenComponent: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp)),
    ) {
        components.forEachIndexed { index, c ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(AppColors.Border),
                )
            }
            ComponentRow(
                component = c,
                interval = c.intervalFor(vehicleType),
                last = lastServiceFor(c.id),
                urgency = if (c.id == "oli_mesin") ReminderUrgency.Overdue else ReminderUrgency.Ok,
                onClick = { onOpenComponent(c.id) },
            )
        }
    }
}

@Composable
private fun ComponentRow(
    component: ComponentInfo,
    interval: String,
    last: String,
    urgency: ReminderUrgency,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(
            icon = component.icon,
            foreground = component.color,
            background = component.color.copy(alpha = 0.13f),
            size = 38.dp,
            iconSize = 20.sp,
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
                if (urgency == ReminderUrgency.Overdue) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(urgency.color()))
                }
            }
            Text(
                text = "Terakhir: $last",
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
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
                text = interval,
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
                text = "Dari katalog atau ketik sendiri",
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
        }
        FaIcon(icon = FaIcons.CHEVRON_RIGHT, color = AppColors.TextSubtle, size = 14.sp)
    }
}

private fun lastServiceFor(id: String): String = when (id) {
    "oli_mesin"    -> "2.420 km lalu · 20 Feb 2026"
    "kampas_rem"   -> "belum tercatat"
    "ban"          -> "4.200 km lalu"
    "busi"         -> "4.600 km lalu"
    "aki"          -> "8 bulan lalu"
    "vbelt"        -> "belum tercatat"
    "rantai"       -> "1.200 km lalu"
    "filter_udara" -> "4.500 km lalu"
    else -> "belum tercatat"
}
