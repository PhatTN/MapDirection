package com.example.phattn.mapdirection.injection

import android.app.Application
import com.example.phattn.mapdirection.MapDirectionApplication
import com.example.phattn.mapdirection.ui.map.MapsActivityModule
import com.example.phattn.mapdirection.ui.search_autocomplete.SearchAutocompleteModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AndroidInjectionModule::class,
        AppModule::class,
        MapsActivityModule::class,
        SearchAutocompleteModule::class,
        ViewModelModule::class
))
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application) : Builder
        fun build() : AppComponent
    }
    fun inject(application: MapDirectionApplication)
}
