package com.zrifapps.goservice.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.HexColor
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingState
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingStep
import com.zrifapps.goservice.feature.onboarding.domain.repository.OnboardingRepository
import com.zrifapps.goservice.feature.onboarding.domain.usecase.AdvanceOnboarding
import com.zrifapps.goservice.feature.onboarding.domain.usecase.CompleteOnboarding
import com.zrifapps.goservice.feature.onboarding.domain.usecase.ObserveOnboarding
import com.zrifapps.goservice.feature.onboarding.domain.usecase.RecordNotificationPermission
import com.zrifapps.goservice.feature.onboarding.domain.usecase.SetOnboardingProfileName
import com.zrifapps.goservice.feature.profile.domain.usecase.EnsureProfileSeeded
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleDraft
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.feature.vehicle.domain.usecase.AddVehicle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingFlowViewModel(
    private val observeOnboarding: ObserveOnboarding,
    private val advanceOnboarding: AdvanceOnboarding,
    private val setOnboardingProfileName: SetOnboardingProfileName,
    private val recordNotifPermission: RecordNotificationPermission,
    private val completeOnboarding: CompleteOnboarding,
    private val ensureProfileSeeded: EnsureProfileSeeded,
    private val addVehicle: AddVehicle,
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<OnboardingEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            observeOnboarding().collect { persisted ->
                merge(persisted)
            }
        }
    }

    fun setNameInput(value: String) {
        _state.update { it.copy(nameInput = value.take(MAX_NAME_LEN), error = null) }
    }

    fun finishCarousel() = advanceTo(OnboardingStep.ProfileName)

    fun submitName() {
        val typed = _state.value.nameInput.trim()
        if (typed.isEmpty()) {
            _state.update { it.copy(error = DomainError.Validation.FieldRequired("name")) }
            return
        }
        launchAction {
            when (val r = setOnboardingProfileName(typed)) {
                is DomainResult.Failure -> fail(r.error)
                is DomainResult.Success -> advance(OnboardingStep.PickVehicleType)
            }
        }
    }

    fun skipName() {
        launchAction { advance(OnboardingStep.PickVehicleType) }
    }

    fun pickVehicleType(type: VehicleType) {
        launchAction {
            when (val r = onboardingRepository.setVehicleType(type.key)) {
                is DomainResult.Failure -> fail(r.error)
                is DomainResult.Success -> advance(OnboardingStep.AddVehicle)
            }
        }
    }

    fun submitVehicle(input: OnboardingVehicleInput) {
        launchAction {
            val profileResult = ensureProfileSeeded(
                EnsureProfileSeeded.Params(name = _state.value.persistedName),
            )
            val profile = (profileResult as? DomainResult.Success)?.data
                ?: return@launchAction fail((profileResult as DomainResult.Failure).error)

            val draft = VehicleDraft(
                nickname = input.nickname.trim(),
                type = input.type,
                subtypeId = Component.UNIVERSAL_SUBTYPE,
                brand = input.brand.trim(),
                model = input.model.trim(),
                year = input.year,
                plateNumber = input.plateNumber.trim(),
                odometer = Distance.ofKm(input.odometerKm),
                color = HexColor.parseOrNull(input.colorHex) ?: EnsureProfileSeeded.DEFAULT_AVATAR_COLOR,
            )
            val vehicleResult = addVehicle(
                AddVehicle.Params(draft = draft, ownerProfileId = profile.id),
            )
            val vehicle = (vehicleResult as? DomainResult.Success)?.data
                ?: return@launchAction fail((vehicleResult as DomainResult.Failure).error)

            onboardingRepository.setFirstVehicleId(vehicle.id)
            advance(OnboardingStep.NotificationPermission)
        }
    }

    fun skipVehicle() {
        launchAction { advance(OnboardingStep.NotificationPermission) }
    }

    fun recordNotificationPermission(asked: Boolean, granted: Boolean) {
        launchAction {
            val r = recordNotifPermission(
                RecordNotificationPermission.Params(asked = asked, granted = granted),
            )
            when (r) {
                is DomainResult.Failure -> fail(r.error)
                is DomainResult.Success -> completeFlow()
            }
        }
    }

    fun goBack(to: OnboardingStep) = advanceTo(to)

    fun skipAll() {
        launchAction { completeFlow() }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun observeState(onChange: (OnboardingUiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    fun observeEvents(onEvent: (OnboardingEvent) -> Unit): Cancellable =
        events.subscribeOn(viewModelScope, onEvent)

    private fun advanceTo(step: OnboardingStep) {
        launchAction { advance(step) }
    }

    private suspend fun advance(step: OnboardingStep) {
        when (val r = advanceOnboarding(step)) {
            is DomainResult.Failure -> fail(r.error)
            is DomainResult.Success -> _events.emit(OnboardingEvent.GoTo(step))
        }
    }

    private suspend fun completeFlow() {
        val seed = ensureProfileSeeded(
            EnsureProfileSeeded.Params(name = _state.value.persistedName),
        )
        if (seed is DomainResult.Failure) return fail(seed.error)
        when (val r = completeOnboarding()) {
            is DomainResult.Failure -> fail(r.error)
            is DomainResult.Success -> _events.emit(OnboardingEvent.CompletedFlow)
        }
    }

    private fun launchAction(block: suspend () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            try {
                block()
            } finally {
                _state.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private suspend fun fail(error: DomainError) {
        _state.update { it.copy(error = error) }
        _events.emit(OnboardingEvent.Failed(error))
    }

    private fun merge(persisted: OnboardingState) {
        _state.update { current ->
            current.copy(
                step = persisted.currentStep,
                persistedName = persisted.profileName,
                nameInput = if (current.nameInput.isEmpty() && persisted.profileName != null) {
                    persisted.profileName
                } else current.nameInput,
                pickedVehicleType = persisted.pickedVehicleType?.let(VehicleType::fromKey),
                firstVehicleId = persisted.firstVehicleId,
            )
        }
    }

    private companion object {
        const val MAX_NAME_LEN = 20
    }
}
