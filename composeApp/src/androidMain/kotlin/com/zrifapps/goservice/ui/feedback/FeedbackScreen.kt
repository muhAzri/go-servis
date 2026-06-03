package com.zrifapps.goservice.ui.feedback

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.feedback.domain.model.FeedbackType
import com.zrifapps.goservice.feature.feedback.presentation.FeedbackViewModel
import com.zrifapps.goservice.ui.common.AppSnackbar
import com.zrifapps.goservice.ui.common.toastError
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.AppInfoBanner
import com.zrifapps.goservice.ui.components.AppTextField
import com.zrifapps.goservice.ui.legal.LegalShell
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun FeedbackScreen(
    onBack: () -> Unit,
    onSent: () -> Unit,
    vm: FeedbackViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

    LaunchedEffect(vm) {
        vm.events.collect { event ->
            when (event) {
                FeedbackViewModel.Event.Submitted -> {
                    AppSnackbar.show("Terima kasih! Masukanmu sudah terkirim.")
                    onSent()
                }
                is FeedbackViewModel.Event.Failed -> ctx.toastError(event.error)
            }
        }
    }

    LegalShell(title = "Kirim Masukan", onBack = onBack) {
        Text(
            text = "Punya saran, keluhan, atau menemukan bug? Ceritakan di sini — kami baca semua masukan.",
            color = AppColors.TextMuted,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TypeSelector(selected = state.type, onSelect = vm::setType)

        Spacer(Modifier.height(18.dp))

        MessageField(
            value = state.message,
            onValueChange = vm::setMessage,
            length = state.messageLen,
            maxLength = state.messageMaxLen,
            placeholder = when (state.type) {
                FeedbackType.Bug ->
                    "Ceritakan bug-nya: langkahnya, apa yang terjadi, dan apa yang kamu harapkan."
                FeedbackType.Feedback ->
                    "Tulis saran, ide, atau keluhanmu di sini."
            },
        )

        Spacer(Modifier.height(18.dp))

        AppTextField(
            label = "Email (opsional)",
            value = state.email,
            onValueChange = vm::setEmail,
            placeholder = "email@kamu.com",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            onImeAction = { vm.submit() },
        )
        if (state.emailError) {
            Text(
                text = "Format email tidak valid",
                color = AppColors.Danger,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        AppInfoBanner(
            text = "Agar mudah ditindaklanjuti, app menyertakan versi app, model HP, dan versi Android. " +
                "Email hanya dipakai untuk membalas masukanmu.",
        )

        Spacer(Modifier.height(24.dp))

        AppButton(
            text = if (state.isSubmitting) "Mengirim…" else "Kirim Masukan",
            onClick = vm::submit,
            enabled = state.canSubmit,
        )
    }
}

@Composable
private fun TypeSelector(
    selected: FeedbackType,
    onSelect: (FeedbackType) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TypeChip(
            modifier = Modifier.weight(1f),
            icon = FaIcons.LIGHTBULB,
            label = "Masukan",
            selected = selected == FeedbackType.Feedback,
            onClick = { onSelect(FeedbackType.Feedback) },
        )
        TypeChip(
            modifier = Modifier.weight(1f),
            icon = FaIcons.TRIANGLE_EXCLAMATION,
            label = "Lapor Bug",
            selected = selected == FeedbackType.Bug,
            onClick = { onSelect(FeedbackType.Bug) },
        )
    }
}

@Composable
private fun TypeChip(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) AppColors.PrimarySofter else AppColors.Surface)
            .border(
                width = 1.5.dp,
                color = if (selected) AppColors.Primary else AppColors.Border,
                shape = RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FaIcon(
            icon = icon,
            color = if (selected) AppColors.Primary else AppColors.TextMuted,
            size = 15.sp,
        )
        Text(
            text = label,
            color = if (selected) AppColors.Primary else AppColors.TextMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun MessageField(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    maxLength: Int,
    placeholder: String,
) {
    val font = plusJakartaSansFontFamily()
    var focused by remember { mutableStateOf(false) }
    val borderColor = if (focused) AppColors.Primary else AppColors.Border

    Column {
        Text(
            text = "Pesan",
            color = AppColors.TextMuted,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            fontFamily = font,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            cursorBrush = SolidColor(AppColors.Primary),
            textStyle = TextStyle(
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = font,
                lineHeight = 21.sp,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focused = it.isFocused },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
                        .background(AppColors.Surface, RoundedCornerShape(14.dp))
                        .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = AppColors.TextSubtle,
                            fontSize = 15.sp,
                            lineHeight = 21.sp,
                            fontFamily = font,
                        )
                    }
                    innerTextField()
                }
            },
        )
        Text(
            text = "$length / $maxLength",
            color = AppColors.TextSubtle,
            fontSize = 11.sp,
            fontFamily = font,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
        )
    }
}
