package com.zrifapps.goservice.feature.feedback.domain.repository

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.feedback.domain.model.FeedbackReport

/**
 * Sends a feedback / bug report to the GoService backend. Implemented per
 * platform (the Android impl gathers device context and performs the HTTP call).
 */
interface FeedbackRepository {
    suspend fun submit(report: FeedbackReport): DomainResult<Unit>
}
