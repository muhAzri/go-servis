package com.zrifapps.goservice.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.R
import com.zrifapps.goservice.feature.onboarding.presentation.AppGate
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import kotlinx.coroutines.delay

private val PrimaryGreen = Color(0xFF2E8B57)
private const val MIN_SPLASH_MS = 900L

@Composable
fun SplashScreen(
    gate: AppGate,
    onResolved: (AppGate) -> Unit,
) {
    LaunchedEffect(gate) {
        if (gate is AppGate.Loading) return@LaunchedEffect
        delay(MIN_SPLASH_MS)
        onResolved(gate)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                FaIcon(
                    icon = FaIcons.WRENCH,
                    color = PrimaryGreen,
                    size = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_name),
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.03).em
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pengingat Servis Motor & Mobil",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
        }
    }
}
