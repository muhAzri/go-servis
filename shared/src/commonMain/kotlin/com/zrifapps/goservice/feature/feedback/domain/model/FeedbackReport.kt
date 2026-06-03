package com.zrifapps.goservice.feature.feedback.domain.model

/** Kind of report the user is sending. [wireValue] matches the web API contract. */
enum class FeedbackType(val wireValue: String) {
    Feedback("feedback"),
    Bug("bug"),
}

/**
 * A user-entered feedback or bug report. Device/app context (version, model, OS)
 * is attached by the data layer, not the user — so it isn't part of this model.
 */
data class FeedbackReport(
    val type: FeedbackType,
    val message: String,
    val email: String?,
)
