package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

data class FilterChipItem(
    val id: String,
    val label: String,
    val icon: String? = null,
    val count: Int? = null,
)

@Composable
fun FilterChipBar(
    chips: List<FilterChipItem>,
    activeId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { chip ->
            val active = chip.id == activeId
            val bg = if (active) AppColors.Primary else AppColors.Surface
            val fg = if (active) Color.White else AppColors.TextPrimary
            val borderColor = if (active) AppColors.Primary else AppColors.Border

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(bg)
                    .border(1.dp, borderColor, RoundedCornerShape(100.dp))
                    .clickable { onSelect(chip.id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (chip.icon != null) {
                    FaIcon(icon = chip.icon, color = fg, size = 12.sp)
                }
                Text(
                    text = chip.label,
                    color = fg,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                )
                if (chip.count != null && chip.count > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (active) Color.White.copy(alpha = 0.2f)
                                else AppColors.PrimarySoft
                            )
                            .padding(horizontal = 7.dp, vertical = 1.dp),
                    ) {
                        Text(
                            text = chip.count.toString(),
                            color = if (active) Color.White else AppColors.Primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font,
                        )
                    }
                }
            }
        }
    }
}

data class ActiveFilterChip(
    val id: String,
    val label: String,
)

@Composable
fun ActiveFilterChips(
    chips: List<ActiveFilterChip>,
    onRemove: (String) -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (chips.isEmpty()) return
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { chip ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(AppColors.PrimarySoft)
                    .border(1.dp, AppColors.Primary.copy(alpha = 0.25f), RoundedCornerShape(100.dp))
                    .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = chip.label,
                    color = AppColors.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                )
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary.copy(alpha = 0.15f))
                        .clickable { onRemove(chip.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    FaIcon(icon = FaIcons.XMARK, color = AppColors.Primary, size = 9.sp)
                }
            }
        }
        Spacer(Modifier.width(2.dp))
        Text(
            text = "Reset",
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable(onClick = onResetAll)
                .padding(horizontal = 4.dp, vertical = 8.dp),
        )
    }
}
