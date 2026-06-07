package com.zrifapps.goservice.ui.onboarding

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.AppTextButton
import com.zrifapps.goservice.ui.components.AppTextField
import com.zrifapps.goservice.ui.onboarding.components.OnboardingStepHeader
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

private const val MAX_NAME_LEN = 20

@Composable
fun NameScreen(
    initialName: String,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val trimmed = initialName.trim()
    val initial = (trimmed.firstOrNull() ?: 'B').uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        OnboardingStepHeader(
            stepLabel = "Langkah 1 dari 4",
            title = "Kami panggil kamu apa?",
            subtitle = "Biar pengingatnya berasa lebih personal. Disimpan lokal, bisa diubah di Pengaturan kapan saja.",
            onBack = onBack,
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(AppColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initial,
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = font,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        AppTextField(
            label = "Nama panggilan",
            value = initialName,
            onValueChange = { if (it.length <= MAX_NAME_LEN) onNameChange(it) },
            placeholder = "Misal: Budi",
            imeAction = ImeAction.Done,
            onImeAction = { if (trimmed.isNotEmpty()) onNext() },
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Cukup nama panggilan saja — tidak perlu nama lengkap.",
                color = AppColors.TextSubtle,
                fontSize = 12.sp,
                fontFamily = font,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${initialName.length}/$MAX_NAME_LEN",
                color = AppColors.TextSubtle,
                fontSize = 11.sp,
                fontFamily = font,
            )
        }

        Spacer(Modifier.weight(1f))

        AppButton(
            text = "Lanjut",
            onClick = onNext,
            enabled = trimmed.isNotEmpty() && !isSubmitting,
            trailingIcon = FaIcons.CHEVRON_RIGHT,
        )

        Spacer(Modifier.height(4.dp))

        AppTextButton(text = "Lewati — pakai \"Kamu\" saja", onClick = onSkip)

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun NameScreenPreview() {
    NameScreen(
        initialName = "",
        isSubmitting = false,
        onBack = {},
        onNameChange = {},
        onNext = {},
        onSkip = {},
    )
}
