package com.zrifapps.goservice.feature.feedback.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.feedback.domain.model.FeedbackReport
import com.zrifapps.goservice.feature.feedback.domain.repository.FeedbackRepository

/**
 * Validates and submits a feedback / bug report. Trims and caps the message,
 * normalises the optional email (blank -> null), and rejects malformed input
 * before hitting the network.
 */
class SubmitFeedback(
    private val repository: FeedbackRepository,
) {
    suspend operator fun invoke(report: FeedbackReport): DomainResult<Unit> {
        val message = report.message.trim()
        if (message.isEmpty()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("pesan"))
        }

        val email = report.email?.trim()?.ifEmpty { null }
        if (email != null && !EMAIL_REGEX.matches(email)) {
            return DomainResult.Failure(
                DomainError.Validation.InvalidFormat("email", "format email tidak valid"),
            )
        }

        return repository.submit(
            report.copy(message = message.take(MAX_MESSAGE_LEN), email = email),
        )
    }

    companion object {
        const val MAX_MESSAGE_LEN: Int = 5000
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
