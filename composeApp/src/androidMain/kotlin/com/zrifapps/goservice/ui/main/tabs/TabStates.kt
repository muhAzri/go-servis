package com.zrifapps.goservice.ui.main.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zrifapps.goservice.ui.components.EmptyState
import com.zrifapps.goservice.ui.components.Skeleton
import com.zrifapps.goservice.ui.components.SkeletonLeading
import com.zrifapps.goservice.ui.theme.FaIcons

// ─────────────────────────── §D · Empty states ───────────────────────────

@Composable
fun HomeTabEmpty(
    modifier: Modifier = Modifier,
    vehicleType: String = "motor",
    onAddVehicle: () -> Unit = {},
) {
    val icon = if (vehicleType == "mobil") FaIcons.CAR else FaIcons.MOTORCYCLE
    Column(modifier = modifier.fillMaxSize()) {
        TabHeader(subtitle = null, title = "Garasi Saya")
        Box(modifier = Modifier.fillMaxSize()) {
            EmptyState(
                icon = icon,
                title = "Belum ada kendaraan",
                body = "Tambah motor atau mobilmu untuk mulai catat servis & dapat pengingat.",
                ctaLabel = "+ Tambah Kendaraan",
                onCta = onAddVehicle,
            )
        }
    }
}

@Composable
fun HistoryTabEmpty(
    modifier: Modifier = Modifier,
    onAddService: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize()) {
        TabHeader(subtitle = null, title = "Riwayat Servis")
        Box(modifier = Modifier.fillMaxSize()) {
            EmptyState(
                icon = FaIcons.HISTORY,
                title = "Belum ada servis tercatat",
                body = "Catat servis pertama untuk mulai melacak biaya & interval per komponen.",
                ctaLabel = "Catat Servis",
                onCta = onAddService,
            )
        }
    }
}

@Composable
fun RemindersTabEmpty(
    modifier: Modifier = Modifier,
    onAddReminder: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize()) {
        TabHeader(subtitle = null, title = "Pengingat Servis")
        Box(modifier = Modifier.fillMaxSize()) {
            EmptyState(
                icon = FaIcons.BELL,
                title = "Belum ada reminder aktif",
                body = "Buat pengingat berdasarkan KM atau tanggal supaya servis tepat waktu.",
                ctaLabel = "+ Buat Pengingat",
                onCta = onAddReminder,
            )
        }
    }
}

@Composable
fun VehicleComponentsEmpty(
    modifier: Modifier = Modifier,
    onPickComponents: () -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        EmptyState(
            icon = FaIcons.WRENCH,
            title = "Belum ada komponen dipantau",
            body = "Pilih komponen yang ingin kamu pantau usianya — kami pakai interval pabrikan.",
            ctaLabel = "Pilih Komponen",
            onCta = onPickComponents,
        )
    }
}

// ─────────────────────────── §D · Loading states ───────────────────────────

@Composable
fun HomeTabLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabHeader(subtitle = null, title = "Garasi Saya")
        Skeleton.Card(height = 160.dp, lines = 2)
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Column {
                Spacer(Modifier.height(8.dp))
                Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
                Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
                Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun HistoryTabLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabHeader(subtitle = null, title = "Riwayat Servis")
        // chip-bar skeleton
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 14.dp)
                .fillMaxWidth()
                .height(36.dp),
        ) {
            Skeleton.Tile(count = 4, columns = 4)
        }
        repeat(5) {
            Skeleton.Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                leading = SkeletonLeading.Icon,
                lines = 2,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun RemindersTabLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabHeader(subtitle = null, title = "Pengingat Servis")
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 14.dp)
                .fillMaxWidth()
                .height(36.dp),
        ) {
            Skeleton.Tile(count = 4, columns = 4)
        }
        repeat(3) {
            Skeleton.Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                leading = SkeletonLeading.Icon,
                lines = 2,
            )
        }
        Spacer(Modifier.height(12.dp))
        repeat(3) {
            Skeleton.Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                leading = SkeletonLeading.Icon,
                lines = 2,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun ComponentDetailLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(top = 16.dp)),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Skeleton.Card(height = 120.dp, lines = 3)
        repeat(3) {
            Skeleton.Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                leading = SkeletonLeading.Icon,
                lines = 2,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}
