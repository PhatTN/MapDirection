package com.example.phattn.mapdirection

import android.app.Application
import timber.log.Timber

class MapDirectionApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
