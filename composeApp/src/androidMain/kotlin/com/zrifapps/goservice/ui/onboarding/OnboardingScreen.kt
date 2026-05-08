package com.zrifapps.goservice.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.FaStyle
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val subtitle: String,
)

private val pages = listOf(
    OnboardingPage(
        title = "Catat Semua\nKendaraanmu",
        subtitle = "Tambah motor atau mobil dan kelola semua servisnya dalam satu tempat.",
    ),
    OnboardingPage(
        title = "Pengingat Servis\nOtomatis",
        subtitle = "Kami ingatkan kapan ganti oli, servis berkala, dan perawatan lainnya tepat waktu.",
    ),
    OnboardingPage(
        title = "Riwayat Servis\nLengkap",
        subtitle = "Catat setiap servis — biaya, bengkel, dan kilometer. Semua tersimpan rapi.",
    ),
)

@Composable
fun OnboardingScreen(
    onSkip: () -> Unit,
    onFinish: () -> Unit,
) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val font = plusJakartaSansFontFamily()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .statusBarsPadding(),
    ) {
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp),
        ) {
            Text(
                text = "Lewati",
                color = AppColors.TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    when (page) {
                        0 -> IllustWelcome()
                        1 -> IllustReminder()
                        2 -> IllustHistory()
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(pages.size) { idx ->
                        val isActive = pagerState.currentPage == idx
                        val dotWidth by animateDpAsState(
                            targetValue = if (isActive) 28.dp else 8.dp,
                            animationSpec = spring(),
                            label = "dot_width_$idx",
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(dotWidth)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isActive) AppColors.Primary else AppColors.Border),
                        )
                    }
                }

                val currentPage = pagerState.currentPage

                Text(
                    text = pages[currentPage].title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = font,
                    color = AppColors.TextPrimary,
                    lineHeight = 34.sp,
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = pages[currentPage].subtitle,
                    fontSize = 15.sp,
                    fontFamily = font,
                    color = AppColors.TextMuted,
                    lineHeight = 22.sp,
                )

                Spacer(Modifier.height(28.dp))

                AppButton(
                    text = if (currentPage == pages.size - 1) "Mulai Sekarang" else "Lanjut",
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                        } else {
                            onFinish()
                        }
                    },
                    trailingIcon = FaIcons.CHEVRON_RIGHT,
                )
            }
        }
    }
}

@Composable
private fun IllustWelcome() {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(260.dp),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(100.dp))
                .background(AppColors.PrimarySoft),
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-16).dp, y = 18.dp),
        ) {
            FaIcon(
                icon = FaIcons.MOTORCYCLE,
                color = AppColors.Primary,
                size = 108.sp,
                style = FaStyle.Solid,
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-22).dp, y = 26.dp)
                .rotate(10f),
        ) {
            FaIcon(
                icon = FaIcons.CAR,
                color = AppColors.PrimaryDark,
                size = 66.sp,
                style = FaStyle.Solid,
            )
        }

        Box(
            modifier = Modifier
                .offset(x = 18.dp, y = 16.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(AppColors.Warning.copy(alpha = 0.7f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 18.dp, y = (-20).dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(AppColors.PrimaryDark.copy(alpha = 0.4f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-18).dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(AppColors.Danger.copy(alpha = 0.5f)),
        )
    }
}

@Composable
private fun IllustReminder() {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(260.dp),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(100.dp))
                .background(AppColors.WarningSoft),
        )

        Box(modifier = Modifier.align(Alignment.Center)) {
            FaIcon(
                icon = FaIcons.BELL,
                color = AppColors.Warning,
                size = 112.sp,
                style = FaStyle.Solid,
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-12).dp, y = 24.dp)
                .zIndex(2f),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AppColors.Danger),
                )
                Text(
                    text = "Ganti oli telat",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary,
                    fontFamily = font,
                )
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 0.dp, y = (-28).dp)
                .zIndex(2f),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AppColors.Warning),
                )
                Text(
                    text = "Servis dalam 3 hari",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary,
                    fontFamily = font,
                )
            }
        }
    }
}

@Composable
private fun IllustHistory() {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(260.dp),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(100.dp))
                .background(AppColors.PrimarySofter),
        )

        HistoryCard(
            iconUnicode = FaIcons.OIL_CAN,
            iconBg = AppColors.WarningSoft,
            iconColor = AppColors.Warning,
            rotation = -2f,
            modifier = Modifier
                .offset(x = 34.dp, y = 84.dp)
                .zIndex(1f),
            font = font,
        )
        HistoryCard(
            iconUnicode = FaIcons.CAR,
            iconBg = AppColors.DangerSoft,
            iconColor = AppColors.Danger,
            rotation = 0f,
            modifier = Modifier
                .offset(x = 40.dp, y = 100.dp)
                .zIndex(2f),
            font = font,
        )
        HistoryCard(
            iconUnicode = FaIcons.WRENCH,
            iconBg = AppColors.PrimarySoft,
            iconColor = AppColors.Primary,
            rotation = 2f,
            modifier = Modifier
                .offset(x = 46.dp, y = 116.dp)
                .zIndex(3f),
            font = font,
        )
    }
}

@Composable
private fun HistoryCard(
    iconUnicode: String,
    iconBg: Color,
    iconColor: Color,
    rotation: Float,
    modifier: Modifier = Modifier,
    font: androidx.compose.ui.text.font.FontFamily,
) {
    Card(
        modifier = modifier
            .width(210.dp)
            .height(62.dp)
            .rotate(rotation),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = iconUnicode, color = iconColor, size = 17.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.SurfaceAlt),
                )
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(AppColors.SurfaceAlt),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(onSkip = {}, onFinish = {})
}
