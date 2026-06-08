package com.zrifapps.goservice.feature.reminder.di

import com.zrifapps.goservice.core.database.GoServiceDatabase
import com.zrifapps.goservice.feature.reminder.data.local.ReminderDao
import com.zrifapps.goservice.feature.reminder.data.repository.ReminderRepositoryImpl
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository
import com.zrifapps.goservice.feature.reminder.domain.usecase.AddReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.CompleteReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.DeleteReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.DismissReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.ObserveReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.ObserveReminders
import com.zrifapps.goservice.feature.reminder.domain.usecase.SnoozeReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.UpdateReminder
import com.zrifapps.goservice.feature.reminder.presentation.AddReminderViewModel
import com.zrifapps.goservice.feature.reminder.presentation.EditReminderViewModel
import com.zrifapps.goservice.feature.reminder.presentation.ReminderDetailViewModel
import com.zrifapps.goservice.feature.reminder.presentation.ReminderListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val reminderModule = module {
    single<ReminderDao> { get<GoServiceDatabase>().reminderDao() }
    single<ReminderRepository> { ReminderRepositoryImpl(get(), get(), get()) }

    factory { ObserveReminders(get()) }
    factory { ObserveReminder(get()) }
    factory { AddReminder(get()) }
    factory { UpdateReminder(get()) }
    factory { SnoozeReminder(get(), get()) }
    factory { CompleteReminder(get()) }
    factory { DismissReminder(get()) }
    factory { DeleteReminder(get()) }

    viewModel { ReminderListViewModel(get()) }
    viewModel { AddReminderViewModel(get(), get(), get(), get(), get()) }
    viewModel { EditReminderViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ReminderDetailViewModel(get(), get(), get(), get(), get(), get()) }
}
