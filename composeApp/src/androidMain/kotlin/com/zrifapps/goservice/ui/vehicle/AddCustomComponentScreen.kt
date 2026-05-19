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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import com.zrifapps.goservice.ui.vehicle.components.ComponentInfo
import com.zrifapps.goservice.ui.vehicle.components.ComponentTag
import com.zrifapps.goservice.ui.vehicle.components.ComponentsCatalog
import com.zrifapps.goservice.ui.vehicle.components.VehicleSubtypes

@Composable
fun AddCustomComponentScreen(
    onBack: () -> Unit,
    onAdd: (String) -> Unit,
    vehicleType: String = "motor",
    subtype: String = "matic",
) {
    var query by remember { mutableStateOf("") }
    val subtypeList = remember(subtype) { ComponentsCatalog.forSubtype(subtype) }
    val results: List<ComponentInfo> = remember(query, subtype) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) subtypeList.filter { it.tag != ComponentTag.Core }
        else ComponentsCatalog.all.filter { it.label.lowercase().contains(q) }
    }
    val suggestions = remember(subtype) {
        subtypeList.filter { it.tag == ComponentTag.Plus || it.tag == ComponentTag.Pro }.take(4)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState()),
    ) {
        TopBar(onBack = onBack)
        SearchField(query = query, onChange = { query = it })
        Spacer(Modifier.height(4.dp))

        if (query.isEmpty() && suggestions.isNotEmpty()) {
            SectionLabel("Saran untuk kamu")
            SuggestionChips(suggestions = suggestions, onPick = { onAdd(it.id) })
            Spacer(Modifier.height(14.dp))
        }

        SectionLabel(if (query.isEmpty()) "Dari katalog" else "${results.size} hasil")
        if (results.isNotEmpty()) {
            ResultsList(
                results = results.take(7),
                vehicleType = vehicleType,
                subtype = subtype,
                onPick = { onAdd(it.id) },
            )
        } else {
            EmptyResults(query = query, onAddNew = { onAdd("custom:$query") })
        }

        Spacer(Modifier.height(16.dp))
        FreeformHint()
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircleIconButton(icon = FaIcons.CHEVRON_LEFT, onClick = onBack)
        Text(
            text = "Tambah komponen",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
        )
    }
}

@Composable
private fun SearchField(query: String, onChange: (String) -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FaIcon(icon = FaIcons.SEARCH, color = AppColors.TextMuted, size = 16.sp)
        Box(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = query,
                onValueChange = onChange,
                singleLine = true,
                cursorBrush = SolidColor(AppColors.Primary),
                textStyle = TextStyle(
                    color = AppColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                ),
            )
            if (query.isEmpty()) {
                Text(
                    text = "Ketik nama komponen…",
                    color = AppColors.TextSubtle,
                    fontSize = 15.sp,
                    fontFamily = font,
                )
            }
        }
        if (query.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onChange("") },
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = FaIcons.XMARK, color = AppColors.TextMuted, size = 14.sp)
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
        letterSpacing = 0.8.sp,
        fontFamily = font,
        modifier = Modifier
            .padding(start = 20.dp, top = 14.dp, bottom = 8.dp),
    )
}

@Composable
private fun SuggestionChips(suggestions: List<ComponentInfo>, onPick: (ComponentInfo) -> Unit) {
    val font = plusJakartaSansFontFamily()
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        suggestions.forEach { s ->
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AppColors.Surface)
                    .border(BorderStroke(1.dp, AppColors.Border), CircleShape)
                    .clickable { onPick(s) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                FaIcon(icon = s.icon, color = s.color, size = 12.sp)
                Text(
                    text = s.label,
                    color = AppColors.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                )
                FaIcon(icon = FaIcons.PLUS, color = AppColors.TextMuted, size = 10.sp)
            }
        }
    }
}

@Composable
private fun ResultsList(
    results: List<ComponentInfo>,
    vehicleType: String,
    subtype: String,
    onPick: (ComponentInfo) -> Unit,
) {
    val subLabel = VehicleSubtypes.labelOf(vehicleType, subtype)
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp)),
    ) {
        results.forEachIndexed { index, c ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(AppColors.Border),
                )
            }
            val inSubtype = c.subtypes.contains("*") || c.subtypes.contains(subtype)
            ResultRow(
                component = c,
                interval = c.intervalFor(vehicleType),
                outOfSubtype = !inSubtype,
                subLabel = subLabel,
                onClick = { onPick(c) },
            )
        }
    }
}

@Composable
private fun ResultRow(
    component: ComponentInfo,
    interval: String,
    outOfSubtype: Boolean,
    subLabel: String,
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
            size = 36.dp,
            iconSize = 18.sp,
            corner = 10.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = component.label,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = interval,
                    color = AppColors.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font,
                )
                if (outOfSubtype) {
                    Text(
                        text = "· bukan tipikal $subLabel",
                        color = AppColors.Warning,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(AppColors.PrimarySoft),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = FaIcons.PLUS, color = AppColors.Primary, size = 14.sp)
        }
    }
}

@Composable
private fun EmptyResults(query: String, onAddNew: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Tidak ada hasil untuk \"$query\"",
            color = AppColors.TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(AppColors.Primary)
                .clickable(onClick = onAddNew)
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FaIcon(icon = FaIcons.PLUS, color = Color.White, size = 14.sp)
            Text(
                text = "Tambahkan \"$query\" sebagai baru",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }
}

@Composable
private fun FreeformHint() {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.SurfaceAlt)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FaIcon(icon = FaIcons.PEN, color = AppColors.TextMuted, size = 14.sp)
        Column {
            Text(
                text = "Tidak ketemu?",
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = "Ketik nama komponen apapun di kolom pencarian — kamu bisa atur intervalnya sendiri.",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
