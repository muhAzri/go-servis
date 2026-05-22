package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

enum class DetailToolbarActionTone { Default, Danger }

data class DetailToolbarAction(
    val icon: String,
    val onTap: () -> Unit,
    val tone: DetailToolbarActionTone = DetailToolbarActionTone.Default,
    val contentDescription: String? = null,
)

@Composable
fun DetailToolbar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    actions: List<DetailToolbarAction> = emptyList(),
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ToolbarSquareButton(
            icon = FaIcons.CHEVRON_LEFT,
            onClick = onBack,
            tint = AppColors.TextPrimary,
            bg = AppColors.Surface,
            border = AppColors.Border,
        )
        if (title != null) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = font,
                modifier = Modifier.weight(1f),
            )
        } else {
            Box(modifier = Modifier.weight(1f))
        }
        actions.forEach { action ->
            val isDanger = action.tone == DetailToolbarActionTone.Danger
            ToolbarSquareButton(
                icon = action.icon,
                onClick = action.onTap,
                tint = if (isDanger) AppColors.Danger else AppColors.TextPrimary,
                bg = if (isDanger) AppColors.DangerSoft else AppColors.Surface,
                border = if (isDanger) AppColors.Danger.copy(alpha = 0.25f) else AppColors.Border,
            )
        }
    }
}

@Composable
private fun ToolbarSquareButton(
    icon: String,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color,
    bg: androidx.compose.ui.graphics.Color,
    border: androidx.compose.ui.graphics.Color,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        FaIcon(icon = icon, color = tint, size = 14.sp)
    }
}
