package com.zrifapps.goservice.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifPermissionSheet(
    onDismiss: () -> Unit,
    onOpenSystemSettings: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AppColors.PrimarySoft),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = FaIcons.BELL, color = AppColors.Primary, size = 32.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Notifikasi dimatikan",
                color = AppColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = font,
                letterSpacing = (-0.3).sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "ServisGo butuh izin notifikasi sistem supaya bisa kirim pengingat servis tepat waktu.",
                color = AppColors.TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.SurfaceAlt)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Step(number = 1, text = "Tap \"Buka Pengaturan\" di bawah")
                Step(number = 2, text = "Pilih \"Notifikasi\" lalu cari ServisGo")
                Step(number = 3, text = "Aktifkan toggle notifikasi utama")
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Primary)
                    .clickable(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            onOpenSystemSettings()
                            onDismiss()
                        }
                    }),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Buka Pengaturan",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Nanti saja",
                color = AppColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
                modifier = Modifier
                    .clickable(onClick = onDismiss)
                    .padding(PaddingValues(horizontal = 14.dp, vertical = 10.dp)),
            )
        }
    }
}

@Composable
private fun Step(number: Int, text: String) {
    val font = plusJakartaSansFontFamily()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(AppColors.Primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = font,
            )
        }
        Text(
            text = text,
            color = AppColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            modifier = Modifier.weight(1f),
        )
    }
}
