package com.zrifapps.goservice.feature.feedback.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.feedback.domain.model.FeedbackReport
import com.zrifapps.goservice.feature.feedback.domain.model.FeedbackType
import com.zrifapps.goservice.feature.feedback.domain.usecase.SubmitFeedback
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val submitFeedback: SubmitFeedback,
) : ViewModel() {

    data class UiState(
        val type: FeedbackType = FeedbackType.Feedback,
        val message: String = "",
        val email: String = "",
        val isSubmitting: Boolean = false,
        val emailError: Boolean = false,
        val messageMaxLen: Int = SubmitFeedback.MAX_MESSAGE_LEN,
    ) {
        val messageLen: Int = message.length
        val canSubmit: Boolean =
            !isSubmitting && message.trim().isNotEmpty() && !emailError
    }

    sealed interface Event {
        data object Submitted : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun setType(type: FeedbackType) {
        _state.update { it.copy(type = type) }
    }

    fun setMessage(value: String) {
        val max = _state.value.messageMaxLen
        val clipped = if (value.length > max) value.take(max) else value
        _state.update { it.copy(message = clipped) }
    }

    fun setEmail(value: String) {
        _state.update { it.copy(email = value, emailError = validateEmail(value)) }
    }

    fun submit() {
        val snapshot = _state.value
        if (!snapshot.canSubmit) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                val report = FeedbackReport(
                    type = snapshot.type,
                    message = snapshot.message,
                    email = snapshot.email.trim().ifEmpty { null },
                )
                when (val result = submitFeedback(report)) {
                    is DomainResult.Success -> _events.emit(Event.Submitted)
                    is DomainResult.Failure -> _events.emit(Event.Failed(result.error))
                }
            } finally {
                _state.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    fun observeEvents(onEvent: (Event) -> Unit): Cancellable =
        events.subscribeOn(viewModelScope, onEvent)

    private fun validateEmail(value: String): Boolean {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return false
        return !EMAIL_REGEX.matches(trimmed)
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
