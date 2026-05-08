package com.cvp.app.di

import com.cvp.app.presentation.main.MainViewModel
import com.cvp.app.presentation.map.MapViewModel
import com.cvp.app.presentation.onboarding.OnboardingViewModel
import com.cvp.app.presentation.settings.SettingsViewModel
import com.cvp.app.presentation.welcome.WelcomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { WelcomeViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { MapViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
