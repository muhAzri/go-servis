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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.profile.presentation.EditProfileViewModel
import com.zrifapps.goservice.feature.profile.presentation.EditProfileViewModel.EmailError
import com.zrifapps.goservice.feature.profile.presentation.EditProfileViewModel.NameError
import com.zrifapps.goservice.feature.service.presentation.ServiceHistoryViewModel
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleListViewModel
import com.zrifapps.goservice.ui.common.toastError
import com.zrifapps.goservice.ui.components.AppTextField
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    vm: EditProfileViewModel = koinViewModel(),
    vehicleVm: VehicleListViewModel = koinViewModel(),
    serviceVm: ServiceHistoryViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val vehicleState by vehicleVm.state.collectAsStateWithLifecycle()
    val serviceState by serviceVm.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

    LaunchedEffect(vm) {
        vm.events.collect { event ->
            when (event) {
                is EditProfileViewModel.Event.Saved -> onSaved()
                is EditProfileViewModel.Event.Failed -> ctx.toastError(event.error)
            }
        }
    }

    val font = plusJakartaSansFontFamily()
    val color = remember(state.avatarColorHex) {
        runCatching { Color(android.graphics.Color.parseColor(state.avatarColorHex)) }
            .getOrDefault(AppColors.Primary)
    }
    val palette = remember(state.palette) {
        state.palette.map { hex ->
            hex to runCatching { Color(android.graphics.Color.parseColor(hex)) }
                .getOrDefault(AppColors.Primary)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        TopBar(
            saveEnabled = state.canSave,
            onBack = onBack,
            onSave = vm::save,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            AvatarPreview(initial = state.initial, color = color, font = font)
            Spacer(Modifier.height(24.dp))

            SectionLabel("Warna avatar")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                palette.forEach { (hex, swatch) ->
                    val selected = hex == state.avatarColorHex
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(swatch)
                            .border(
                                BorderStroke(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) AppColors.TextPrimary else AppColors.Border,
                                ),
                                RoundedCornerShape(14.dp),
                            )
                            .clickable { vm.setAvatarColor(hex) },
                    )
                }
            }
            Spacer(Modifier.height(22.dp))

            AppTextField(
                label = "Nama panggilan",
                value = state.name,
                onValueChange = vm::setName,
                placeholder = "Misal: Budi",
                imeAction = ImeAction.Next,
            )
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = state.nameError?.let(::nameErrorLabel)
                        ?: "Dipakai untuk sapaan di Beranda. Tidak dikirim ke server.",
                    color = if (state.nameError != null) AppColors.Danger else AppColors.TextSubtle,
                    fontSize = 12.sp,
                    fontFamily = font,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${state.nameLen}/${state.nameMaxLen}",
                    color = AppColors.TextSubtle,
                    fontSize = 11.sp,
                    fontFamily = font,
                )
            }
            Spacer(Modifier.height(18.dp))

            AppTextField(
                label = "Email (opsional · untuk backup)",
                value = state.email,
                onValueChange = vm::setEmail,
                placeholder = "alamat@email.com",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = state.emailError?.let(::emailErrorLabel)
                    ?: "Dipakai untuk recovery data kalau kamu ganti HP atau hapus app.",
                color = if (state.emailError != null) AppColors.Danger else AppColors.TextSubtle,
                fontSize = 12.sp,
                fontFamily = font,
            )
            Spacer(Modifier.height(22.dp))

            StatsCard(
                font = font,
                vehicleCount = vehicleState.vehicles.size,
                serviceCount = serviceState.totalCount,
                sinceLabel = EditProfileViewModel.formatSinceLabel(state.sinceEpochMillis),
            )
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
                    .clickable(onClick = vm::resetToDefaults),
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
private fun AvatarPreview(initial: String, color: Color, font: FontFamily) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
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
}

@Composable
private fun TopBar(saveEnabled: Boolean, onBack: () -> Unit, onSave: () -> Unit) {
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
                .background(if (saveEnabled) AppColors.Primary else AppColors.Primary.copy(alpha = 0.45f))
                .clickable(enabled = saveEnabled, onClick = onSave)
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
private fun StatsCard(
    font: FontFamily,
    vehicleCount: Int,
    serviceCount: Int,
    sinceLabel: String,
) {
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
            StatItem(vehicleCount.toString(), "Kendaraan", font, Modifier.weight(1f))
            StatItem(serviceCount.toString(), "Servis", font, Modifier.weight(1f))
            StatItem(sinceLabel, "Sejak", font, Modifier.weight(1f))
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

private fun nameErrorLabel(error: NameError): String = when (error) {
    NameError.Required -> "Nama wajib diisi"
    NameError.TooLong -> "Nama terlalu panjang"
}

private fun emailErrorLabel(error: EmailError): String = when (error) {
    EmailError.InvalidFormat -> "Format email tidak valid"
}
