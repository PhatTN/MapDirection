package com.example.phattn.mapdirection.ui.search_autocomplete

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SearchAutocompleteModule {
    @ContributesAndroidInjector
    abstract fun contributeSearchAutocompleteActivity() : SearchAutocompleteActivity
}