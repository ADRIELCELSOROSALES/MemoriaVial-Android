package com.cvp.app

import android.app.Application
import com.cvp.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.maplibre.android.MapLibre
import timber.log.Timber

class CvpApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        MapLibre.getInstance(this)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@CvpApplication)
            modules(appModule)
        }
    }
}
