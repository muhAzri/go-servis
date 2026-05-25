package com.zrifapps.goservice.ui.vehicle

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.zrifapps.goservice.feature.onboarding.presentation.PresentationFactory
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.ui.components.ActionSheet
import com.zrifapps.goservice.ui.components.ActionSheetOption
import com.zrifapps.goservice.ui.components.ActionSheetSelectionMode
import com.zrifapps.goservice.ui.theme.FaIcons

@Composable
fun ShareVehicleSheet(
    vehicle: Vehicle?,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val name = vehicle?.displayTitle?.takeIf(String::isNotBlank) ?: "kendaraan"

    ActionSheet(
        title = "Bagikan $name",
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
                subtitle = "WhatsApp, email, dll.",
                icon = FaIcons.SHARE,
            ),
        ),
        selectionMode = ActionSheetSelectionMode.Tap,
        onDismiss = onDismiss,
        onSelect = { opt ->
            val v = vehicle ?: return@ActionSheet
            val text = PresentationFactory.vehicleShareText(v)
            when (opt.value) {
                "copy" -> {
                    clipboard.setText(AnnotatedString(text))
                    Toast.makeText(context, "Ringkasan disalin", Toast.LENGTH_SHORT).show()
                }
                "system" -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, name)
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    context.startActivity(Intent.createChooser(intent, "Bagikan $name"))
                }
            }
        },
    )
}
