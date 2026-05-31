package com.zrifapps.goservice.feature.backup.di

import com.zrifapps.goservice.feature.backup.domain.usecase.BuildBackupCsv
import com.zrifapps.goservice.feature.backup.domain.usecase.ImportBackupCsv
import com.zrifapps.goservice.feature.backup.presentation.BackupViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val backupModule = module {
    factory { BuildBackupCsv(get(), get(), get(), get(), get()) }
    factory { ImportBackupCsv(get(), get(), get(), get()) }

    viewModel { BackupViewModel(get(), get(), get(), get(), get(), get()) }
}
