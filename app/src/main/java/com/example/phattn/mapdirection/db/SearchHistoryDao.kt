package com.example.phattn.mapdirection.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.phattn.mapdirection.model.SearchHistory
import com.example.phattn.mapdirection.model.SearchPrediction

/**
 * Interface for database access on SearchHistory related operations
 */
@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(history: SearchHistory)

    @Query("SELECT * FROM search_prediction " +
            "WHERE place_id IN (SELECT place_id FROM search_history " +
            "ORDER BY search_time DESC LIMIT :p0,:p1)")
    fun getHistories(page: Int, size: Int) : LiveData<List<SearchPrediction>>

}