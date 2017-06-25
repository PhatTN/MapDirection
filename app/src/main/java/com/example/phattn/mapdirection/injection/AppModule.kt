package com.example.phattn.mapdirection.injection

import android.app.Application
import android.arch.persistence.room.Room
import com.example.phattn.mapdirection.AppConstants
import com.example.phattn.mapdirection.api.DirectionService
import com.example.phattn.mapdirection.db.MapDirectionDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(ViewModelModule::class))
class AppModule {
    @Singleton
    @Provides
    fun provideDb(app: Application) = Room.databaseBuilder(
            app, MapDirectionDb::class.java, AppConstants.DATABASE_FILENAME).build()

    @Singleton
    @Provides
    fun provideSearchPredictionDao(db: MapDirectionDb) = db.searchPredictionDao()

    @Singleton
    @Provides
    fun providePlaceDao(db: MapDirectionDb) = db.placeDao()

    @Singleton
    @Provides
    fun provideSearchHistoryDao(db: MapDirectionDb) = db.searchHistoryDao()

    @Singleton
    @Provides
    fun provideDirectionService() = DirectionService.Creator.newDirectionService()
}
