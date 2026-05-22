package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun FormShell(
    title: String,
    onClose: () -> Unit,
    onSave: () -> Unit,
    saveEnabled: Boolean = true,
    saveLabel: String = "Simpan",
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.Surface)
                    .border(1.dp, AppColors.Border, RoundedCornerShape(12.dp))
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = FaIcons.XMARK, color = AppColors.TextPrimary, size = 14.sp)
            }
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = font,
                modifier = Modifier.weight(1f),
            )
            val bgColor = if (saveEnabled) AppColors.Primary else AppColors.Primary.copy(alpha = 0.35f)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(bgColor)
                    .then(if (saveEnabled) Modifier.clickable(onClick = onSave) else Modifier)
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = saveLabel,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp)),
        ) {
            content()
        }

        if (footer != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Surface)
                    .border(1.dp, AppColors.Border, RoundedCornerShape(0.dp))
                    .padding(16.dp),
            ) {
                footer()
            }
        }
    }
}
