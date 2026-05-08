package com.cvp.app.di

import com.cvp.app.data.local.preferences.OnboardingPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferencesModule = module {
    single { OnboardingPreferences(androidContext()) }
}
