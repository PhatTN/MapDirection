package com.example.phattn.mapdirection.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.example.phattn.mapdirection.AppExecutor
import com.example.phattn.mapdirection.db.SearchHistoryDao
import com.example.phattn.mapdirection.model.Resource
import com.example.phattn.mapdirection.model.SearchHistory
import com.example.phattn.mapdirection.model.SearchPrediction
import org.threeten.bp.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepository @Inject constructor(appExecutor: AppExecutor,
                                                  searchHistoryDao: SearchHistoryDao) {

    private val mAppExecutor = appExecutor
    private val mSearchHistoryDao = searchHistoryDao

    private val mHistories = MediatorLiveData<Resource<List<SearchPrediction>>>()

    fun saveSearchHistory(placeId: String) {
        mAppExecutor.diskIO().execute {
            mSearchHistoryDao.insert(SearchHistory(placeId, LocalDateTime.now()))
        }
    }

    fun getHistories(page: Int, size: Int) : LiveData<Resource<List<SearchPrediction>>> {
        mHistories.value = Resource.loading(null)
        mAppExecutor.diskIO().execute {
            val result = mSearchHistoryDao.getHistories(page, size)
            mHistories.addSource(result, { data ->
                mHistories.value = Resource.success(data)
            })
        }

        return mHistories
    }

}
