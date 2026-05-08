package com.zrifapps.goservice.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AppInfoBanner
import com.zrifapps.goservice.ui.onboarding.components.OnboardingStepHeader
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.FaStyle
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun PickVehicleTypeScreen(
    onBack: () -> Unit,
    onPickType: (String) -> Unit,
) {
    val font = plusJakartaSansFontFamily()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        OnboardingStepHeader(
            stepLabel = "Langkah 1 dari 3",
            title = "Kendaraan kamu apa?",
            subtitle = "Bisa tambah lebih dari satu nanti.",
            onBack = onBack,
        )

        Spacer(Modifier.height(28.dp))

        VehicleTypeCard(
            icon = FaIcons.MOTORCYCLE,
            label = "Motor",
            sub = "Bebek, matic, sport",
            onClick = { onPickType("motor") },
            font = font,
        )

        Spacer(Modifier.height(14.dp))

        VehicleTypeCard(
            icon = FaIcons.CAR,
            label = "Mobil",
            sub = "MPV, SUV, sedan, hatchback",
            onClick = { onPickType("mobil") },
            font = font,
        )

        Spacer(Modifier.weight(1f))

        AppInfoBanner("Data disimpan lokal di HP. Tidak butuh login.")

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun VehicleTypeCard(
    icon: String,
    label: String,
    sub: String,
    onClick: () -> Unit,
    font: androidx.compose.ui.text.font.FontFamily,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(AppColors.Surface)
            .border(1.5.dp, AppColors.Border, RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(AppColors.PrimarySoft),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = icon, color = AppColors.Primary, size = 32.sp, style = FaStyle.Solid)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
                color = AppColors.TextPrimary,
            )
            Text(
                text = sub,
                fontSize = 13.sp,
                fontFamily = font,
                color = AppColors.TextMuted,
            )
        }

        FaIcon(icon = FaIcons.CHEVRON_RIGHT, color = AppColors.TextSubtle, size = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
private fun PickVehicleTypeScreenPreview() {
    PickVehicleTypeScreen(onBack = {}, onPickType = {})
}
