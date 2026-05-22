package com.zrifapps.goservice.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

enum class PullRefreshState { Pulling, Releasing, Refreshing }

@Composable
fun PullRefreshIndicator(
    state: PullRefreshState,
    modifier: Modifier = Modifier,
) {
    val font = plusJakartaSansFontFamily()
    val transition = rememberInfiniteTransition(label = "pull-refresh-spin")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "spin-angle",
    )

    val rotation = when (state) {
        PullRefreshState.Refreshing -> angle
        PullRefreshState.Releasing -> 180f
        PullRefreshState.Pulling -> 0f
    }

    val label = when (state) {
        PullRefreshState.Refreshing -> "Memuat ulang…"
        PullRefreshState.Releasing -> "Lepas untuk refresh"
        PullRefreshState.Pulling -> "Tarik ke bawah"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(
            modifier = Modifier
                .size(24.dp)
                .rotate(rotation),
        ) {
            val stroke = 2.5.dp.toPx()
            val diameter = size.minDimension - stroke
            // Track
            drawArc(
                color = AppColors.PrimarySoft,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(diameter, diameter),
                style = Stroke(width = stroke),
            )
            // Top arc indicator
            drawArc(
                color = AppColors.Primary,
                startAngle = -90f,
                sweepAngle = if (state == PullRefreshState.Refreshing) 270f else 90f,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(diameter, diameter),
                style = Stroke(width = stroke),
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
    }
}
