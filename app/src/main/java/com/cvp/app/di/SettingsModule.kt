package com.cvp.app.di

import com.cvp.app.data.repository.SettingsRepositoryImpl
import com.cvp.app.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val settingsModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(androidContext()) }
}
