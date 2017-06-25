package com.example.phattn.mapdirection.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.phattn.mapdirection.model.SearchPrediction

/**
 * Interface for database access on SearchPrediction related operations
 */
@Dao
interface SearchingPredictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(prediction: SearchPrediction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPredictions(predictions: List<SearchPrediction>)

    // TODO have a bug with parameter names in Kotlin's annotation processing. So we using :p0 instead for parameter name
    // Will replace :p0 when that bug is fixed
    @Query("SELECT place_id, full_text, primary_text, secondary_text FROM search_prediction "
            + "WHERE primary_text LIKE :p0")
    fun searchPredictions(query: String) : LiveData<List<SearchPrediction>>
}