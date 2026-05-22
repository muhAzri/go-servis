package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

data class ShareCardData(
    val serviceLabel: String,
    val serviceIcon: String,
    val vehicleName: String,
    val vehiclePlate: String,
    val vehicleSpec: String,
    val vehicleIcon: String,
    val dateLabel: String,
    val kmLabel: String,
    val costLabel: String,
    val workshopLabel: String,
    val recordId: String = "S0001",
)

@Composable
fun ShareCardPoster(
    data: ShareCardData,
    modifier: Modifier = Modifier,
) {
    val font = plusJakartaSansFontFamily()
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF2E8B57), Color(0xFF22683F), Color(0xFF1A4A2A)),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = FaIcons.WRENCH, color = Color(0xFF22683F), size = 14.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "ServisGo",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.2).sp,
                fontFamily = font,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "BUKTI SERVIS",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
                fontFamily = font,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = data.serviceIcon, color = Color.White, size = 28.sp)
            }
            Text(
                text = "SERVIS",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = font,
            )
            Text(
                text = data.serviceLabel,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp,
                lineHeight = 22.sp,
                fontFamily = font,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(PaddingValues(horizontal = 12.dp, vertical = 10.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FaIcon(icon = data.vehicleIcon, color = Color.White, size = 18.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.vehicleName,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
                Text(
                    text = "${data.vehiclePlate} · ${data.vehicleSpec}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaTile(label = "TANGGAL", value = data.dateLabel, modifier = Modifier.weight(1f), mono = false)
                MetaTile(label = "KM", value = data.kmLabel, modifier = Modifier.weight(1f), mono = true)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaTile(label = "BIAYA", value = data.costLabel, modifier = Modifier.weight(1f), mono = true)
                MetaTile(label = "BENGKEL", value = data.workshopLabel, modifier = Modifier.weight(1f), mono = false)
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.18f)),
        ) {}
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dicatat dengan ServisGo",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontFamily = font,
                )
                Text(
                    text = "servisgo.app · #${data.recordId.uppercase()}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "servisgo.app",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}

@Composable
private fun MetaTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    mono: Boolean,
) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            fontFamily = font,
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp,
            fontFamily = if (mono) FontFamily.Monospace else font,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}
