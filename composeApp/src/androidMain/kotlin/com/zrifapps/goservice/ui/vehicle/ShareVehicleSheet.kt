package com.zrifapps.goservice.ui.vehicle

import androidx.compose.runtime.Composable
import com.zrifapps.goservice.ui.components.ActionSheet
import com.zrifapps.goservice.ui.components.ActionSheetOption
import com.zrifapps.goservice.ui.components.ActionSheetSelectionMode
import com.zrifapps.goservice.ui.theme.FaIcons

@Composable
fun ShareVehicleSheet(
    vehicleName: String,
    onDismiss: () -> Unit,
    onCopy: () -> Unit = {},
    onSystemShare: () -> Unit = {},
) {
    ActionSheet(
        title = "Bagikan $vehicleName",
        subtitle = "Pilih cara membagikan ringkasan kendaraan.",
        options = listOf(
            ActionSheetOption(
                value = "copy",
                label = "Salin ringkasan teks",
                subtitle = "Tempel di chat / catatan",
                icon = FaIcons.COPY,
            ),
            ActionSheetOption(
                value = "system",
                label = "Bagikan via aplikasi lain",
                subtitle = "Buka share sheet sistem",
                icon = FaIcons.SHARE,
            ),
        ),
        selectionMode = ActionSheetSelectionMode.Tap,
        onDismiss = onDismiss,
        onSelect = { opt ->
            when (opt.value) {
                "copy" -> onCopy()
                "system" -> onSystemShare()
            }
        },
    )
}
