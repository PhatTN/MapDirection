package com.example.phattn.mapdirection.injection

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.phattn.mapdirection.ui.map.MapsViewModel
import com.example.phattn.mapdirection.ui.search_autocomplete.SearchAutocompleteViewModel
import com.example.phattn.mapdirection.viewmodel.MapDirectionViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchAutocompleteViewModel::class)
    abstract fun bindSearchAutocompleteViewModel(searchAutocompleteViewModel: SearchAutocompleteViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapsViewModel::class)
    abstract fun bindMapsViewModel(mapViewModel: MapsViewModel) : ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: MapDirectionViewModelFactory) : ViewModelProvider.Factory

}