package com.cvp.app.di

import org.koin.dsl.module

val appModule = module {
    includes(serializationModule, dataModule, locationModule, preferencesModule, settingsModule, presentationModule)
}
