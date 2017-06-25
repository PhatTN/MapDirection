package com.example.phattn.mapdirection.repository

import android.arch.lifecycle.LiveData
import com.example.phattn.mapdirection.AppExecutor
import com.example.phattn.mapdirection.api.ApiResponse
import com.example.phattn.mapdirection.api.PlaceService
import com.example.phattn.mapdirection.db.SearchingPredictionDao
import com.example.phattn.mapdirection.model.Resource
import com.example.phattn.mapdirection.model.SearchPrediction
import com.example.phattn.mapdirection.util.DbQueryUtil
import com.example.phattn.mapdirection.util.RateLimiter
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.maps.model.LatLngBounds
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchPredictionRepository @Inject constructor(appExecutor: AppExecutor,
                                                     predictionDao: SearchingPredictionDao,
                                                     placeService: PlaceService) {

    private val mAppExecutor = appExecutor
    private val mPredictionDao = predictionDao
    private val mPlaceService = placeService

    private var mPredictionsRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun getPredictions(googleApiClient: GoogleApiClient, query: String, bounds: LatLngBounds,
                       filters: AutocompleteFilter): LiveData<Resource<List<SearchPrediction>>> {
        return object: NetworkBoundResource<List<SearchPrediction>, List<SearchPrediction>>(mAppExecutor) {

            override fun saveCallResult(item: List<SearchPrediction>) {
                mPredictionDao.insertPredictions(item)
            }

            override fun shouldFetch(data: List<SearchPrediction>?): Boolean {
                return data == null || data.isEmpty() || mPredictionsRateLimit.shouldFetch(query)
            }

            override fun loadFromDb(): LiveData<List<SearchPrediction>> {
                return mPredictionDao.searchPredictions(DbQueryUtil.buildParamForSearch(query))
            }

            override fun createCall(): LiveData<ApiResponse<List<SearchPrediction>>> {
                return mPlaceService.getPredictions(googleApiClient, query, bounds, filters)
            }

        }.asLiveData()
    }

}