package com.zrifapps.goservice.ui.main.sheets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.zrifapps.goservice.ui.components.ConfirmDialog
import com.zrifapps.goservice.ui.components.ConfirmDialogTone
import com.zrifapps.goservice.ui.theme.FaIcons

private enum class WipeStep { Warn, Confirm }

@Composable
fun WipeDataFlow(
    onDismiss: () -> Unit,
    onConfirmed: () -> Unit,
) {
    var step by remember { mutableStateOf(WipeStep.Warn) }

    when (step) {
        WipeStep.Warn -> {
            ConfirmDialog(
                title = "Hapus SEMUA data?",
                body = "Semua kendaraan, riwayat servis, pengingat, dan komponen kustom akan dihapus permanen dari perangkat ini. Tindakan ini tidak bisa dibatalkan.",
                icon = FaIcons.SHIELD_HALVED,
                tone = ConfirmDialogTone.Danger,
                confirmLabel = "Lanjutkan",
                cancelLabel = "Batal",
                onDismiss = onDismiss,
                onConfirm = { step = WipeStep.Confirm },
            )
        }
        WipeStep.Confirm -> {
            ConfirmDialog(
                title = "Konfirmasi terakhir",
                body = "Untuk melanjutkan, ketik teks di bawah persis seperti yang ditampilkan.",
                icon = FaIcons.TRASH,
                tone = ConfirmDialogTone.Danger,
                confirmLabel = "HAPUS SEMUA",
                cancelLabel = "Batal",
                requiresText = "HAPUS SEMUA",
                onDismiss = onDismiss,
                onConfirm = {
                    onConfirmed()
                    onDismiss()
                },
            )
        }
    }
}
