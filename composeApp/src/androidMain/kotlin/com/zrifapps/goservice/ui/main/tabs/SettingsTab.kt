package com.zrifapps.goservice.ui.main.tabs

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.profile.presentation.ProfileViewModel
import com.zrifapps.goservice.feature.service.presentation.ServiceHistoryViewModel
import com.zrifapps.goservice.feature.settings.presentation.SettingsViewModel
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleListViewModel
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import org.koin.androidx.compose.koinViewModel
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.zrifapps.goservice.core.notification.OdometerNotifier
import com.zrifapps.goservice.core.notification.ReminderNotifier
import com.zrifapps.goservice.feature.backup.presentation.BackupViewModel
import com.zrifapps.goservice.ui.main.sheets.readTextFromUri
import kotlinx.coroutines.launch

private data class SettingItem(
    val icon: String,
    val label: String,
    val danger: Boolean = false,
    val toggleState: Boolean? = null,
    val onToggle: ((Boolean) -> Unit)? = null,
    val detail: String? = null,
    val onClick: (() -> Unit)? = null,
)

private data class SettingSection(
    val title: String,
    val items: List<SettingItem>,
)

@Composable
fun SettingsTab(
    modifier: Modifier = Modifier,
    onOpenPrivacy: () -> Unit = {},
    onOpenTerms: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenEditProfile: () -> Unit = {},
    onOpenNotifSheet: () -> Unit = {},
    profileVm: ProfileViewModel = koinViewModel(),
    vehicleVm: VehicleListViewModel = koinViewModel(),
    serviceVm: ServiceHistoryViewModel = koinViewModel(),
    settingsVm: SettingsViewModel = koinViewModel(),
    backupVm: BackupViewModel = koinViewModel(),
) {
    val profileState by profileVm.state.collectAsStateWithLifecycle()
    val vehicleState by vehicleVm.state.collectAsStateWithLifecycle()
    val serviceState by serviceVm.state.collectAsStateWithLifecycle()
    val settingsState by settingsVm.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pendingNotifAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val notifPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val pending = pendingNotifAction
        pendingNotifAction = null
        if (granted) {
            pending?.invoke()
        } else {
            Toast.makeText(
                context,
                "Izin notifikasi ditolak. Aktifkan di Setelan agar notif muncul.",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    fun ensureNotifPermissionThen(action: () -> Unit) {
        val enabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        if (enabled) {
            action()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pendingNotifAction = action
            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Pre-13: no runtime permission, but user may have disabled the channel/app in Settings.
            Toast.makeText(
                context,
                "Notifikasi GoService dimatikan. Buka Setelan untuk mengaktifkan.",
                Toast.LENGTH_LONG,
            ).show()
            runCatching {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }.onFailure {
                val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val text = readTextFromUri(context, uri)
                val message = if (text == null) {
                    "Gagal membaca file"
                } else {
                    val result = backupVm.importCsv(text)
                    if (result.malformed) {
                        "File CSV tidak dikenali"
                    } else {
                        "Impor selesai: ${result.imported} ditambah · ${result.skipped} dilewati"
                    }
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    val displayName = profileState.displayName
    val userColor = remember(profileState.avatarColorHex) {
        profileState.avatarColorHex?.let(::parseHexColorOrNull) ?: AppColors.Primary
    }
    val vehicleCount = vehicleState.vehicles.size
    val serviceCount = serviceState.records.size

    var showExportSheet by remember { mutableStateOf(false) }
    var showWipeDialog by remember { mutableStateOf(false) }

    val sections = listOf(
        SettingSection(
            title = "Akun & Data",
            items = listOf(
                SettingItem(
                    icon = FaIcons.FILE,
                    label = "Ekspor Data (CSV)",
                    onClick = { showExportSheet = true },
                ),
                SettingItem(
                    icon = FaIcons.FILE,
                    label = "Impor Data (CSV)",
                    onClick = { importLauncher.launch(arrayOf("*/*")) },
                ),
                SettingItem(
                    icon = FaIcons.TRASH,
                    label = "Hapus Semua Data",
                    danger = true,
                    onClick = { showWipeDialog = true },
                ),
            ),
        ),
        SettingSection(
            title = "Notifikasi",
            items = listOf(
                SettingItem(
                    icon = FaIcons.BELL,
                    label = "Pengingat servis",
                    toggleState = settingsState.serviceReminderNotificationsEnabled,
                    onToggle = { settingsVm.setServiceReminderNotificationsEnabled(it) },
                ),
                SettingItem(
                    icon = FaIcons.GAUGE,
                    label = "Pengingat update KM",
                    toggleState = settingsState.odometerReminderNotificationsEnabled,
                    onToggle = { settingsVm.setOdometerReminderNotificationsEnabled(it) },
                ),
                SettingItem(
                    icon = FaIcons.BELL,
                    label = "Tes notif servis",
                    detail = "Kirim sekarang",
                    onClick = {
                        ensureNotifPermissionThen {
                            val firstVehicle = vehicleState.vehicles.firstOrNull()
                            val sampleId = firstVehicle?.id?.let { "test:$it" } ?: "test:reminder"
                            ReminderNotifier.show(
                                context = context,
                                reminderId = sampleId,
                                title = "Tes pengingat servis",
                                body = "Kalau ini muncul, channel \"Pengingat servis\" sudah aktif.",
                            )
                            Toast.makeText(context, "Notif servis dikirim", Toast.LENGTH_SHORT).show()
                        }
                    },
                ),
                SettingItem(
                    icon = FaIcons.GAUGE,
                    label = "Tes notif update KM",
                    detail = "Kirim sekarang",
                    onClick = {
                        val firstVehicle = vehicleState.vehicles.firstOrNull()
                        if (firstVehicle == null) {
                            Toast.makeText(context, "Tambah kendaraan dulu", Toast.LENGTH_SHORT).show()
                        } else {
                            ensureNotifPermissionThen {
                                OdometerNotifier.show(
                                    context = context,
                                    vehicleId = firstVehicle.id,
                                    title = "Tes update KM ${firstVehicle.displayTitle}",
                                    body = "Kalau ini muncul, channel \"Update KM\" sudah aktif. Tap untuk buka layar Update KM.",
                                )
                                Toast.makeText(context, "Notif update KM dikirim", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                ),
            ),
        ),
        SettingSection(
            title = "Legal & Bantuan",
            items = listOf(
                SettingItem(
                    icon = FaIcons.LOCK,
                    label = "Kebijakan Privasi",
                    onClick = onOpenPrivacy,
                ),
                SettingItem(
                    icon = FaIcons.FILE,
                    label = "Syarat & Ketentuan",
                    onClick = onOpenTerms,
                ),
                SettingItem(
                    icon = FaIcons.CIRCLE_INFO,
                    label = "Tentang GoService",
                    onClick = onOpenAbout,
                ),
                SettingItem(
                    icon = FaIcons.CIRCLE_INFO,
                    label = "Bantuan & FAQ",
                    onClick = onOpenHelp,
                ),
                SettingItem(
                    icon = FaIcons.STAR,
                    label = "Beri Rating ⭐",
                    onClick = {},
                ),
            ),
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabHeader(subtitle = null, title = "Pengaturan")

        ProfileHeaderCard(
            userName = displayName,
            avatarColor = userColor,
            vehicleCount = vehicleCount,
            serviceCount = serviceCount,
            onClick = onOpenEditProfile,
        )

        Spacer(Modifier.height(8.dp))

        sections.forEach { section ->
            SectionTitle(section.title)
            SectionCard {
                section.items.forEachIndexed { index, item ->
                    if (index > 0) RowDivider()
                    SettingRow(item = item)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Text(
            text = "GoService v1.0.0 · build 2026.05.06\n© 2026 Muhammad Azri Fatihah Susanto",
            color = AppColors.TextSubtle,
            fontSize = 11.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
    }

    if (showExportSheet) {
        com.zrifapps.goservice.ui.main.sheets.ExportCsvSheet(
            onDismiss = { showExportSheet = false },
            onShare = { showExportSheet = false },
        )
    }
    if (showWipeDialog) {
        com.zrifapps.goservice.ui.main.sheets.WipeDataFlow(
            onDismiss = { showWipeDialog = false },
            onConfirmed = { showWipeDialog = false },
        )
    }
}

@Composable
private fun ProfileHeaderCard(
    userName: String,
    avatarColor: Color,
    vehicleCount: Int,
    serviceCount: Int,
    onClick: () -> Unit,
) {
    val displayName = userName.ifBlank { "Kamu" }
    val initial = (userName.trim().firstOrNull() ?: 'K').uppercase()

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, AppColors.Border, RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(avatarColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayName,
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Profil lokal · $vehicleCount kendaraan · $serviceCount servis tercatat",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(AppColors.PrimarySoft)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FaIcon(icon = FaIcons.PEN, color = AppColors.Primary, size = 12.sp)
            Text(
                text = "Edit",
                color = AppColors.Primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        color = AppColors.TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(
            PaddingValues(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 6.dp),
        ),
    )
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, AppColors.Border, RoundedCornerShape(18.dp))
            .background(AppColors.Surface),
    ) {
        content()
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AppColors.Border),
    )
}

@Composable
private fun SettingRow(item: SettingItem) {
    val rowModifier = if (item.onClick != null) {
        Modifier.clickable(onClick = item.onClick)
    } else {
        Modifier
    }

    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FaIcon(
            icon = item.icon,
            color = if (item.danger) AppColors.Danger else AppColors.TextMuted,
            size = 18.sp,
        )
        Text(
            text = item.label,
            modifier = Modifier.weight(1f),
            color = if (item.danger) AppColors.Danger else AppColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
        when {
            item.toggleState != null -> {
                ToggleSwitch(
                    checked = item.toggleState,
                    onCheckedChange = { item.onToggle?.invoke(it) },
                )
            }
            item.detail != null -> {
                Text(
                    text = item.detail,
                    color = AppColors.TextMuted,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.width(4.dp))
                FaIcon(
                    icon = FaIcons.CHEVRON_RIGHT,
                    color = AppColors.TextSubtle,
                    size = 12.sp,
                )
            }
            item.onClick != null -> {
                FaIcon(
                    icon = FaIcons.CHEVRON_RIGHT,
                    color = AppColors.TextSubtle,
                    size = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val knobOffset by animateDpAsState(targetValue = if (checked) 20.dp else 2.dp, label = "knob")
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(if (checked) AppColors.Primary else AppColors.SurfaceAlt)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

private fun parseHexColorOrNull(hex: String): Color? = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (_: Throwable) {
    null
}
