package com.zrifapps.goservice.feature.reminder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderFilter
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderSort
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency
import com.zrifapps.goservice.feature.reminder.domain.usecase.ObserveReminders
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class ReminderListViewModel(
    private val observeReminders: ObserveReminders,
) : ViewModel() {

    data class UiState(
        val reminders: List<Reminder> = emptyList(),
        val isLoading: Boolean = true,
        val urgencyFilter: Set<ReminderUrgency> = emptySet(),
        val query: String = "",
        val sort: ReminderSort = ReminderSort.UrgencyDesc,
    ) {
        val isEmpty: Boolean get() = !isLoading && reminders.isEmpty() &&
            urgencyFilter.isEmpty() && query.isBlank()
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val filterFlow = MutableStateFlow(ReminderFilter())
    private val sortFlow = MutableStateFlow(ReminderSort.UrgencyDesc)

    init {
        combine(
            filterFlow.debounce { f ->
                if (f.query.isNullOrBlank()) 0L else QUERY_DEBOUNCE_MS
            },
            sortFlow,
        ) { filter, sort -> filter to sort }
            .flatMapLatest { (filter, sort) ->
                observeReminders(ObserveReminders.Params(filter = filter, sort = sort))
            }
            .onEach { list ->
                _state.update { it.copy(reminders = list, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun setUrgencyFilter(urgencies: Set<ReminderUrgency>) {
        _state.update { it.copy(urgencyFilter = urgencies) }
        filterFlow.update { it.copy(urgencies = urgencies) }
    }

    fun setQuery(query: String) {
        _state.update { it.copy(query = query) }
        filterFlow.update { it.copy(query = query) }
    }

    fun setSort(sort: ReminderSort) {
        _state.update { it.copy(sort = sort) }
        sortFlow.value = sort
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    private companion object {
        const val QUERY_DEBOUNCE_MS = 200L
    }
}
