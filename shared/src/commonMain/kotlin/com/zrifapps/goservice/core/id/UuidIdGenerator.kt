package com.zrifapps.goservice.core.id

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UuidIdGenerator : IdGenerator {
    @OptIn(ExperimentalUuidApi::class)
    override fun newId(): String = Uuid.random().toString()
}
