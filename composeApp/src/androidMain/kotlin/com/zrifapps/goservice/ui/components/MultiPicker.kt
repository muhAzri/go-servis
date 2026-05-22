package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

data class MultiPickerItem(
    val id: String,
    val label: String,
    val subtitle: String? = null,
    val icon: String? = null,
    val color: Color? = null,
)

data class MultiPickerGroup(
    val title: String,
    val items: List<MultiPickerItem>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiPicker(
    title: String,
    groups: List<MultiPickerGroup>,
    initiallySelected: Set<String>,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit,
    subtitle: String? = null,
    searchPlaceholder: String = "Cari…",
    confirmLabelPrefix: String = "Pilih",
) {
    val font = plusJakartaSansFontFamily()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(initiallySelected) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(AppColors.Border),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
        ) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = font,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = AppColors.TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font,
                    lineHeight = 18.sp,
                )
            }
            Spacer(Modifier.height(12.dp))

            PHInput(
                value = query,
                onValueChange = { query = it },
                placeholder = searchPlaceholder,
                leadingIcon = FaIcons.MAGNIFYING_GLASS,
            )
            Spacer(Modifier.height(12.dp))

            val filteredGroups = groups.mapNotNull { g ->
                val items = if (query.isBlank()) g.items
                else g.items.filter {
                    it.label.contains(query, ignoreCase = true) ||
                        (it.subtitle?.contains(query, ignoreCase = true) ?: false)
                }
                if (items.isEmpty()) null else g.copy(items = items)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                filteredGroups.forEach { group ->
                    item(key = "header_${group.title}") {
                        Text(
                            text = group.title.uppercase(),
                            color = AppColors.TextSubtle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            fontFamily = font,
                            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                        )
                    }
                    items(group.items, key = { it.id }) { item ->
                        val checked = item.id in selected
                        MultiPickerRow(
                            item = item,
                            checked = checked,
                            onToggle = {
                                selected = if (checked) selected - item.id else selected + item.id
                            },
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onConfirm(selected)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    disabledContainerColor = AppColors.Primary.copy(alpha = 0.35f),
                ),
                enabled = selected.isNotEmpty(),
            ) {
                Text(
                    text = "$confirmLabelPrefix (${selected.size})",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}

@Composable
private fun MultiPickerRow(
    item: MultiPickerItem,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val accent = item.color ?: AppColors.Primary
    val accentSoft = accent.copy(alpha = 0.13f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) AppColors.PrimarySofter else AppColors.SurfaceAlt)
            .border(
                1.5.dp,
                if (checked) AppColors.Primary else Color.Transparent,
                RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (item.icon != null) {
            IconBadge(
                icon = item.icon,
                foreground = accent,
                background = accentSoft,
                size = 36.dp,
                iconSize = 16.sp,
                corner = 10.dp,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.label,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
            )
            if (item.subtitle != null) {
                Text(
                    text = item.subtitle,
                    color = AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font,
                    lineHeight = 16.sp,
                )
            }
        }
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (checked) AppColors.Primary else Color.Transparent)
                .border(
                    1.5.dp,
                    if (checked) AppColors.Primary else AppColors.Border,
                    RoundedCornerShape(6.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                FaIcon(icon = FaIcons.CHECK, color = Color.White, size = 11.sp)
            }
        }
    }
}
