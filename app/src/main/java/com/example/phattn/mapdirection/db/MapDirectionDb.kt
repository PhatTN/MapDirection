package com.example.phattn.mapdirection.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.example.phattn.mapdirection.model.GeoPlace
import com.example.phattn.mapdirection.model.SearchHistory
import com.example.phattn.mapdirection.model.SearchPrediction

/**
 * DB for whole app
 */
@Database(entities = arrayOf(SearchPrediction::class,
        GeoPlace::class, SearchHistory::class), version = 3 )
@TypeConverters(value = MapDirectionTypeConverts::class)
abstract class MapDirectionDb : RoomDatabase() {

    abstract fun searchPredictionDao() : SearchingPredictionDao
    abstract fun placeDao() : PlaceDao
    abstract fun searchHistoryDao() : SearchHistoryDao

}
