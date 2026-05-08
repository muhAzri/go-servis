package com.zrifapps.goservice.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import goservice.composeapp.generated.resources.Res
import goservice.composeapp.generated.resources.fa_brands_400
import goservice.composeapp.generated.resources.fa_regular_400
import goservice.composeapp.generated.resources.fa_solid_900
import org.jetbrains.compose.resources.Font

enum class FaStyle { Solid, Regular, Brands }

@Composable
fun faFontFamily(style: FaStyle = FaStyle.Solid) = when (style) {
    FaStyle.Solid -> FontFamily(Font(Res.font.fa_solid_900, FontWeight.Normal))
    FaStyle.Regular -> FontFamily(Font(Res.font.fa_regular_400, FontWeight.Normal))
    FaStyle.Brands -> FontFamily(Font(Res.font.fa_brands_400, FontWeight.Normal))
}

@Composable
fun FaIcon(
    icon: String,
    modifier: Modifier = Modifier,
    style: FaStyle = FaStyle.Solid,
    color: Color = LocalTextStyle.current.color,
    size: TextUnit = LocalTextStyle.current.fontSize,
) {
    Text(
        text = icon,
        modifier = modifier,
        color = color,
        fontSize = size,
        fontFamily = faFontFamily(style),
    )
}

@Suppress("unused")
object FaIcons {
    const val HOUSE = ""
    const val BARS = ""
    const val CHEVRON_RIGHT = ""
    const val CHEVRON_LEFT = ""
    const val CHEVRON_DOWN = ""
    const val CHEVRON_UP = ""
    const val ARROW_LEFT = ""
    const val XMARK = ""
    const val CHECK = ""
    const val PLUS = ""
    const val MINUS = ""
    const val ELLIPSIS = ""
    const val SEARCH = ""

    const val PEN = ""
    const val TRASH = ""
    const val COPY = ""
    const val SHARE = ""
    const val DOWNLOAD = ""
    const val UPLOAD = ""
    const val FILTER = ""
    const val SORT = ""

    const val WRENCH = ""
    const val SCREWDRIVER_WRENCH = ""
    const val TOOLBOX = ""
    const val OIL_CAN = ""
    const val GAS_PUMP = ""
    const val CAR = ""
    const val CAR_SIDE = ""
    const val MOTORCYCLE = ""
    const val ROAD = ""
    const val GAUGE = ""

    const val CALENDAR = ""
    const val CALENDAR_CHECK = ""
    const val CALENDAR_PLUS = ""
    const val CLOCK = ""
    const val ROTATE_LEFT = ""
    const val HISTORY = ""

    const val BELL = ""
    const val BELL_SLASH = ""
    const val CIRCLE_CHECK = ""
    const val CIRCLE_XMARK = ""
    const val TRIANGLE_EXCLAMATION = ""
    const val CIRCLE_INFO = ""

    const val USER = ""
    const val USERS = ""
    const val GEAR = ""
    const val LOCK = ""
    const val UNLOCK = ""

    const val LIST = ""
    const val GRIP = ""
    const val STAR = ""
    const val HEART = ""
    const val LOCATION_DOT = ""
    const val PHONE = ""
    const val ENVELOPE = ""
    const val IMAGE = ""
    const val FILE = ""
    const val FOLDER = ""

    const val BRAND_GOOGLE = ""
    const val BRAND_APPLE = ""
    const val BRAND_ANDROID = ""
    const val BRAND_WHATSAPP = ""
}
