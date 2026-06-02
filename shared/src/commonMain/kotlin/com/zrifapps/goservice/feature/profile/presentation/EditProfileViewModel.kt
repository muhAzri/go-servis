package com.zrifapps.goservice.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.value.HexColor
import com.zrifapps.goservice.feature.profile.domain.model.Profile
import com.zrifapps.goservice.feature.profile.domain.model.ProfileDraft
import com.zrifapps.goservice.feature.profile.domain.repository.ProfileRepository
import com.zrifapps.goservice.feature.profile.domain.usecase.EnsureProfileSeeded
import com.zrifapps.goservice.feature.profile.domain.usecase.UpdateProfile
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

class EditProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val updateProfile: UpdateProfile,
    private val ensureProfileSeeded: EnsureProfileSeeded,
) : ViewModel() {

    enum class NameError { Required, TooLong }
    enum class EmailError { InvalidFormat }

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val name: String = "",
        val avatarColorHex: String = DEFAULT_COLOR_HEX,
        val email: String = "",
        val nameError: NameError? = null,
        val emailError: EmailError? = null,
        val sinceEpochMillis: Long? = null,
        val palette: List<String> = PALETTE,
        val nameMaxLen: Int = MAX_NAME_LEN,
    ) {
        val initial: String = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "K"
        val nameLen: Int = name.length
        val canSave: Boolean = !isLoading && !isSaving &&
            nameError == null && emailError == null &&
            name.trim().isNotEmpty()
    }

    sealed interface Event {
        data object Saved : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var hydrated: Boolean = false
    private var currentProfile: Profile? = null

    init {
        viewModelScope.launch {
            profileRepository.observeCurrent().collect { profile ->
                currentProfile = profile
                if (!hydrated) {
                    hydrated = true
                    if (profile != null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                name = profile.name,
                                avatarColorHex = profile.avatarColor.value,
                                email = profile.email.orEmpty(),
                                sinceEpochMillis = profile.createdAt,
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun setName(value: String) {
        val clipped = if (value.length > MAX_NAME_LEN) value.take(MAX_NAME_LEN) else value
        _state.update { it.copy(name = clipped, nameError = validateName(clipped)) }
    }

    fun setAvatarColor(hex: String) {
        if (HexColor.isValid(hex)) {
            _state.update { it.copy(avatarColorHex = hex) }
        }
    }

    fun setEmail(value: String) {
        _state.update { it.copy(email = value, emailError = validateEmail(value)) }
    }

    fun resetToDefaults() {
        _state.update {
            it.copy(
                name = "",
                avatarColorHex = DEFAULT_COLOR_HEX,
                email = "",
                nameError = null,
                emailError = null,
            )
        }
    }

    fun save() {
        val snapshot = _state.value
        if (!snapshot.canSave) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val existing = currentProfile ?: seedNow(snapshot) ?: return@launch
                val updated = existing.copy(
                    name = snapshot.name.trim(),
                    avatarColor = HexColor(snapshot.avatarColorHex),
                    email = snapshot.email.trim().ifEmpty { null },
                )
                when (val result = updateProfile(updated)) {
                    is DomainResult.Success -> _events.emit(Event.Saved)
                    is DomainResult.Failure -> emitFailure(result.error)
                }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    fun observeEvents(onEvent: (Event) -> Unit): Cancellable =
        events.subscribeOn(viewModelScope, onEvent)

    private suspend fun seedNow(snapshot: UiState): Profile? {
        val seeded = ensureProfileSeeded(
            EnsureProfileSeeded.Params(
                name = snapshot.name.trim().ifEmpty { null },
                avatarColor = HexColor(snapshot.avatarColorHex),
            ),
        )
        return when (seeded) {
            is DomainResult.Success -> seeded.data.also { currentProfile = it }
            is DomainResult.Failure -> {
                emitFailure(seeded.error)
                null
            }
        }
    }

    private suspend fun emitFailure(error: DomainError) {
        _events.emit(Event.Failed(error))
    }

    private fun validateName(value: String): NameError? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> NameError.Required
            trimmed.length > MAX_NAME_LEN -> NameError.TooLong
            else -> null
        }
    }

    private fun validateEmail(value: String): EmailError? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return null
        return if (EMAIL_REGEX.matches(trimmed)) null else EmailError.InvalidFormat
    }

    companion object {
        const val MAX_NAME_LEN: Int = 20
        const val DEFAULT_COLOR_HEX: String = "#2E8B57"
        val PALETTE: List<String> = listOf(
            "#2E8B57",
            "#D6453A",
            "#3FB1D6",
            "#E89C2E",
            "#7B6FE8",
            "#1A2418",
        )
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

        private val INDONESIAN_MONTHS_SHORT = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
            "Jul", "Agu", "Sep", "Okt", "Nov", "Des",
        )

        fun formatSinceLabel(epochMillis: Long?): String {
            if (epochMillis == null) return "—"
            val dt = Instant.fromEpochMilliseconds(epochMillis)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val month = INDONESIAN_MONTHS_SHORT[dt.month.number - 1]
            val yearSuffix = (dt.year % 100).toString().padStart(2, '0')
            return "$month '$yearSuffix"
        }
    }
}
