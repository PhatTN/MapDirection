package com.example.phattn.mapdirection.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.phattn.mapdirection.model.GeoPlace

/**
 * Interface for database access on Place related operations
 */
@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(place: GeoPlace)

    @Query("SELECT * FROM place WHERE id = :p0")
    fun getPlaceById(placeId: String) : LiveData<GeoPlace>
}
