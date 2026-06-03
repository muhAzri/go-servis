package com.zrifapps.goservice.feature.feedback.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Request body for POST /api/feedback. Field names match the web API contract. */
@Serializable
data class FeedbackRequestDto(
    val type: String,
    val message: String,
    val email: String? = null,
    @SerialName("app_version") val appVersion: String? = null,
    val platform: String? = null,
    @SerialName("device_model") val deviceModel: String? = null,
    @SerialName("os_version") val osVersion: String? = null,
)
