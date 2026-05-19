package com.zrifapps.goservice.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AppTextField
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

private const val MAX_NAME_LEN = 20

private val AvatarPalette = listOf(
    AppColors.Primary,
    Color(0xFFD6453A),
    Color(0xFF3FB1D6),
    Color(0xFFE89C2E),
    Color(0xFF7B6FE8),
    Color(0xFF1A2418),
)

@Composable
fun EditProfileScreen(
    initialName: String,
    initialColorArgb: Int,
    onBack: () -> Unit,
    onSave: (name: String, colorArgb: Int) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var color by remember { mutableStateOf(Color(initialColorArgb)) }

    val font = plusJakartaSansFontFamily()
    val initial = (name.trim().firstOrNull() ?: 'B').uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        TopBar(
            onBack = onBack,
            onSave = { onSave(name.trim(), color.toArgb()) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(color),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initial,
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = font,
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Inisial dari nama panggilanmu",
                    color = AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontFamily = font,
                )
            }

            SectionLabel("Warna avatar")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AvatarPalette.forEach { c ->
                    val selected = c == color
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(c)
                            .border(
                                BorderStroke(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) AppColors.TextPrimary else AppColors.Border,
                                ),
                                RoundedCornerShape(14.dp),
                            )
                            .clickable { color = c },
                    )
                }
            }
            Spacer(Modifier.height(22.dp))

            AppTextField(
                label = "Nama panggilan",
                value = name,
                onValueChange = { if (it.length <= MAX_NAME_LEN) name = it },
                placeholder = "Misal: Budi",
                imeAction = ImeAction.Done,
            )
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Dipakai untuk sapaan di Beranda. Tidak dikirim ke server.",
                    color = AppColors.TextSubtle,
                    fontSize = 12.sp,
                    fontFamily = font,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${name.length}/$MAX_NAME_LEN",
                    color = AppColors.TextSubtle,
                    fontSize = 11.sp,
                    fontFamily = font,
                )
            }
            Spacer(Modifier.height(18.dp))

            SectionLabel("Email (opsional · untuk backup)")
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Surface)
                    .border(1.5.dp, AppColors.Border, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text = "tambahkan untuk backup ke Drive",
                    color = AppColors.TextSubtle,
                    fontSize = 15.sp,
                    fontFamily = font,
                )
            }
            Spacer(Modifier.height(22.dp))

            StatsCard(font = font)
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        AppColors.Danger.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp),
                    )
                    .clickable {
                        name = ""
                        color = AppColors.Primary
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Reset ke pengaturan awal",
                    color = AppColors.Danger,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }

        Box(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars))
    }
}

@Composable
private fun TopBar(onBack: () -> Unit, onSave: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircleIconButton(icon = FaIcons.CHEVRON_LEFT, onClick = onBack)
        Text(
            text = "Edit Profil",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .height(36.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(AppColors.Primary)
                .clickable(onClick = onSave)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Simpan",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    val font = plusJakartaSansFontFamily()
    Text(
        text = text.uppercase(),
        color = AppColors.TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        fontFamily = font,
    )
}

@Composable
private fun StatsCard(font: FontFamily) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.SurfaceAlt)
            .padding(16.dp),
    ) {
        Text(
            text = "AKUN KAMU",
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            fontFamily = font,
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatItem("4", "Kendaraan", font, Modifier.weight(1f))
            StatItem("12", "Servis", font, Modifier.weight(1f))
            StatItem("Jan '26", "Sejak", font, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, font: FontFamily, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = value,
            color = AppColors.TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.4).sp,
            fontFamily = FontFamily.Monospace,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontFamily = font,
        )
    }
}
