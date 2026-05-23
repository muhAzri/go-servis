package com.zrifapps.goservice.core.id

fun interface IdGenerator {
    fun newId(): String
}
