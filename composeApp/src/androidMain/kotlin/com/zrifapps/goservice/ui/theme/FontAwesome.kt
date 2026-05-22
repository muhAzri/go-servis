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

    // Additional icons used by Core Flow screens
    const val LIFE_RING        = "\uF1CD"
    const val CAR_BATTERY      = "\uF5DF"
    const val CIRCLE_NOTCH     = "\uF1CE"
    const val TEMPERATURE_HALF = "\uF2C9"
    const val GEARS            = "\uE5FE"
    const val BOLT             = "\uF0E7"
    const val LIGHTBULB        = "\uF0EB"
    const val FIRE             = "\uF06D"
    const val ARROW_RIGHT      = "\uF061"
    const val BOX_OPEN         = "\uF49E"
    const val SHIELD_HALVED    = "\uF3ED"
    const val GLOBE            = "\uF0AC"
    const val MOON             = "\uF186"
    const val PEN_TO_SQUARE    = "\uF044"
    const val FILE_LINES       = "\uF15C"
    const val CIRCLE_QUESTION  = "\uF059"

    // Plot-holes primitives additions
    const val MAGNIFYING_GLASS = "\uF002"
    const val ROTATE           = "\uF2F1"
    const val ARROW_ROTATE_RIGHT = "\uF2F9"
    const val SLIDERS          = "\uF1DE"
    const val ARROW_DOWN_SHORT_WIDE = "\uF884"
}
