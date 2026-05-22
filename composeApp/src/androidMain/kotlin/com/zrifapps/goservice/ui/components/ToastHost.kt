package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class ToastTone { Default, Success, Danger }

@Immutable
class ToastHostController internal constructor(
    val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    fun show(
        message: String,
        tone: ToastTone = ToastTone.Default,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
    ) {
        scope.launch {
            val visuals = ToastVisuals(
                message = message,
                tone = tone,
                actionLabel = actionLabel,
            )
            val result = hostState.showSnackbar(visuals)
            if (result == SnackbarResult.ActionPerformed) {
                onAction?.invoke()
            }
        }
    }
}

@Composable
fun rememberToastHost(): ToastHostController {
    val hostState = remember { SnackbarHostState() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    return remember(hostState, scope) { ToastHostController(hostState, scope) }
}

@Composable
fun ToastHost(
    controller: ToastHostController,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = controller.hostState,
        modifier = modifier,
    ) { data ->
        val visuals = data.visuals as? ToastVisuals
        if (visuals != null) {
            ToastCard(data = data, visuals = visuals)
        } else {
            Snackbar(data)
        }
    }
}

private class ToastVisuals(
    override val message: String,
    val tone: ToastTone,
    override val actionLabel: String?,
) : androidx.compose.material3.SnackbarVisuals {
    override val duration = androidx.compose.material3.SnackbarDuration.Short
    override val withDismissAction = false
}

@Composable
private fun ToastCard(data: SnackbarData, visuals: ToastVisuals) {
    val font = plusJakartaSansFontFamily()
    val bg = when (visuals.tone) {
        ToastTone.Success -> AppColors.Primary
        ToastTone.Danger -> AppColors.Danger
        ToastTone.Default -> Color(0xFF1F2A1B)
    }
    val icon = when (visuals.tone) {
        ToastTone.Success -> FaIcons.CIRCLE_CHECK
        ToastTone.Danger -> FaIcons.TRIANGLE_EXCLAMATION
        ToastTone.Default -> null
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (icon != null) {
            FaIcon(icon = icon, color = Color.White, size = 15.sp)
        }
        Text(
            text = visuals.message,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
            modifier = Modifier.weight(1f),
            lineHeight = 18.sp,
        )
        val actionLabel = visuals.actionLabel
        if (actionLabel != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .clickable { data.performAction() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = actionLabel,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}
