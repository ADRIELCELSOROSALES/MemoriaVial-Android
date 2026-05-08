package com.cvp.app.di

import com.cvp.app.data.location.LocationService
import com.cvp.app.data.repository.LocationRepositoryImpl
import com.cvp.app.domain.repository.LocationRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val locationModule = module {
    single { LocationService(androidContext()) }
    single<LocationRepository> { LocationRepositoryImpl(get()) }
}
