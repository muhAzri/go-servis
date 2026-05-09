package com.zrifapps.goservice.ui.main.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors

@Composable
fun TabHeader(
    subtitle: String?,
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(
            PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
        ),
    ) {
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = AppColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Text(
            text = title,
            color = AppColors.TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.4).sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
