package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.AppTheme
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons

enum class BottomTab(val icon: String, val label: String) {
    Beranda(FaIcons.HOUSE, "Beranda"),
    Pengingat(FaIcons.BELL, "Pengingat"),
    Add(FaIcons.PLUS, ""),
    Riwayat(FaIcons.HISTORY, "Riwayat"),
    Saya(FaIcons.GEAR, "Saya"),
}

@Composable
fun BottomNavBar(
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(AppColors.Surface)
            .drawBehind {
                drawLine(
                    color = AppColors.Border,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1f,
                )
            }
            .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        BottomTab.entries.forEach { tab ->
            if (tab == BottomTab.Add) {
                AddTabItem(onClick = { onSelect(tab) })
            } else {
                TabItem(
                    tab = tab,
                    active = tab == selected,
                    onClick = { onSelect(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    tab: BottomTab,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (active) AppColors.Primary else AppColors.TextSubtle
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        FaIcon(icon = tab.icon, color = color, size = 22.sp)
        Text(
            text = tab.label,
            color = color,
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun AddTabItem(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = AppColors.Primary,
                spotColor = AppColors.Primary,
            )
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        FaIcon(icon = FaIcons.PLUS, color = Color.White, size = 26.sp)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF7F2)
@Composable
private fun BottomNavBarPreview() {
    AppTheme {
        BottomNavBar(selected = BottomTab.Beranda, onSelect = {})
    }
}
