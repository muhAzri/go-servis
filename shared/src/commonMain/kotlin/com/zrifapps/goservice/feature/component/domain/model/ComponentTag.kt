package com.zrifapps.goservice.feature.component.domain.model

enum class ComponentTag(val key: String) {
    Core("core"),
    Plus("plus"),
    Pro("pro");

    companion object {
        fun fromKey(key: String): ComponentTag = entries.firstOrNull { it.key == key } ?: Plus
    }
}
