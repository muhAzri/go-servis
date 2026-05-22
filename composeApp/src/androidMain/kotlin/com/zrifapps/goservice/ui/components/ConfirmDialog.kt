package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

enum class ConfirmDialogTone { Default, Danger }

@Composable
fun ConfirmDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    icon: String? = null,
    tone: ConfirmDialogTone = ConfirmDialogTone.Default,
    confirmLabel: String = "Lanjutkan",
    cancelLabel: String = "Batal",
    requiresText: String? = null,
) {
    val font = plusJakartaSansFontFamily()
    val isDanger = tone == ConfirmDialogTone.Danger
    val accent = if (isDanger) AppColors.Danger else AppColors.Primary
    val accentSoft = if (isDanger) AppColors.DangerSoft else AppColors.PrimarySoft

    var typed by remember { mutableStateOf("") }
    val confirmEnabled = requiresText == null || typed == requiresText

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = AppColors.Surface,
        tonalElevation = 0.dp,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(accentSoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        FaIcon(icon = icon, color = accent, size = 22.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                }
                Text(
                    text = title,
                    color = AppColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = font,
                    textAlign = TextAlign.Center,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = body,
                    color = AppColors.TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                )
                if (requiresText != null) {
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Ketik \"$requiresText\" untuk konfirmasi",
                        color = AppColors.TextSubtle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = font,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    BasicTextField(
                        value = typed,
                        onValueChange = { typed = it },
                        singleLine = true,
                        cursorBrush = SolidColor(accent),
                        textStyle = TextStyle(
                            color = AppColors.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = font,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppColors.SurfaceAlt)
                                    .border(
                                        1.5.dp,
                                        if (typed.isEmpty()) AppColors.Border else accent,
                                        RoundedCornerShape(12.dp),
                                    )
                                    .padding(PaddingValues(horizontal = 14.dp, vertical = 12.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                innerTextField()
                            }
                        },
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = confirmEnabled,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    disabledContainerColor = accent.copy(alpha = 0.35f),
                ),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp),
            ) {
                FaIcon(icon = FaIcons.CHECK, color = Color.White, size = 13.sp)
                Spacer(Modifier.size(8.dp))
                Text(
                    text = confirmLabel,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(100.dp),
            ) {
                Text(
                    text = cancelLabel,
                    color = AppColors.TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                )
            }
        },
    )
}
