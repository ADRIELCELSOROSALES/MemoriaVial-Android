package com.cvp.app.di

import com.cvp.app.data.local.GeoJsonLoader
import com.cvp.app.data.local.ZoneLoader
import com.cvp.app.data.repository.RiskZoneRepositoryImpl
import com.cvp.app.domain.repository.RiskZoneRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<ZoneLoader> { GeoJsonLoader(androidContext(), get()) }
    single<RiskZoneRepository> { RiskZoneRepositoryImpl(get()) }
}
