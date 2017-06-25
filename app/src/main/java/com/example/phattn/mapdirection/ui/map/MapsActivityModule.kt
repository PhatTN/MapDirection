package com.example.phattn.mapdirection.ui.map

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MapsActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeMapsActivity() : MapsActivity
}
