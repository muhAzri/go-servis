package com.zrifapps.goservice.core.value

import kotlin.jvm.JvmInline

@JvmInline
value class HexColor(val value: String) {
    init {
        require(isValid(value)) { "Invalid hex color: $value" }
    }

    companion object {
        private val pattern = Regex("^#([0-9A-Fa-f]{6}|[0-9A-Fa-f]{8})$")
        fun isValid(value: String): Boolean = pattern.matches(value)
        fun parseOrNull(value: String?): HexColor? = value?.takeIf(::isValid)?.let(::HexColor)
    }
}
