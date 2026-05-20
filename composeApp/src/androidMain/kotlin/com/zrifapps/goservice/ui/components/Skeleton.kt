package com.zrifapps.goservice.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zrifapps.goservice.ui.theme.AppColors

@Composable
private fun shimmerBrush(widthPx: Float = 600f): Brush {
    val transition = rememberInfiniteTransition(label = "skeleton-shimmer")
    val offset by transition.animateFloat(
        initialValue = -widthPx,
        targetValue = widthPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "skeleton-offset",
    )
    val base = AppColors.SurfaceAlt
    val highlight = AppColors.Border.copy(alpha = 0.55f).compositeOver(AppColors.SurfaceAlt)
    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = androidx.compose.ui.geometry.Offset(offset - widthPx, 0f),
        end = androidx.compose.ui.geometry.Offset(offset, 0f),
    )
}

private fun Color.compositeOver(background: Color): Color {
    val a = alpha + background.alpha * (1f - alpha)
    if (a == 0f) return Color.Transparent
    val r = (red * alpha + background.red * background.alpha * (1f - alpha)) / a
    val g = (green * alpha + background.green * background.alpha * (1f - alpha)) / a
    val b = (blue * alpha + background.blue * background.alpha * (1f - alpha)) / a
    return Color(r, g, b, a)
}

@Composable
private fun ShimmerBlock(
    modifier: Modifier = Modifier,
    corner: Dp = 6.dp,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(corner))
            .background(shimmerBrush()),
    )
}

enum class SkeletonLeading { None, Icon, Avatar }

object Skeleton {

    @Composable
    fun Row(
        modifier: Modifier = Modifier,
        leading: SkeletonLeading = SkeletonLeading.Icon,
        lines: Int = 2,
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (leading) {
                SkeletonLeading.Icon -> ShimmerBlock(
                    modifier = Modifier.size(44.dp),
                    corner = 12.dp,
                )
                SkeletonLeading.Avatar -> Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(shimmerBrush()),
                )
                SkeletonLeading.None -> { /* none */ }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(12.dp),
                )
                if (lines >= 2) {
                    ShimmerBlock(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(10.dp),
                    )
                }
            }
        }
    }

    @Composable
    fun Card(
        modifier: Modifier = Modifier,
        lines: Int = 3,
        height: Dp = 120.dp,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(14.dp),
            )
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height - 60.dp),
                corner = 12.dp,
            )
            if (lines >= 3) {
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(10.dp),
                )
            }
        }
    }

    @Composable
    fun Tile(
        modifier: Modifier = Modifier,
        count: Int = 6,
        columns: Int = 3,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val rows = (count + columns - 1) / columns
            repeat(rows) { rowIdx ->
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(columns) { colIdx ->
                        val index = rowIdx * columns + colIdx
                        if (index < count) {
                            ShimmerBlock(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(82.dp),
                                corner = 14.dp,
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
