package com.zrifapps.goservice.feature.feedback.di

import com.zrifapps.goservice.feature.feedback.domain.usecase.SubmitFeedback
import com.zrifapps.goservice.feature.feedback.presentation.FeedbackViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// Note: FeedbackRepository is bound per-platform (see platformModule).
val feedbackModule = module {
    factory { SubmitFeedback(get()) }
    viewModel { FeedbackViewModel(get()) }
}
