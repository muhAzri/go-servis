package com.zrifapps.goservice.ui.onboarding.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AppBackButton
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun OnboardingStepHeader(
    stepLabel: String,
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val font = plusJakartaSansFontFamily()

    Column(modifier = modifier.fillMaxWidth()) {
        AppBackButton(onClick = onBack)

        Spacer(Modifier.height(8.dp))

        Text(
            text = stepLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
            color = AppColors.TextMuted,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            color = AppColors.TextPrimary,
            lineHeight = 32.sp,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = subtitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = font,
            color = AppColors.TextMuted,
            lineHeight = 21.sp,
        )
    }
}
