package com.example.phattn.mapdirection.repository

import android.arch.lifecycle.LiveData
import com.example.phattn.mapdirection.AppExecutor
import com.example.phattn.mapdirection.api.ApiResponse
import com.example.phattn.mapdirection.api.PlaceService
import com.example.phattn.mapdirection.db.PlaceDao
import com.example.phattn.mapdirection.model.GeoPlace
import com.example.phattn.mapdirection.model.Resource
import com.example.phattn.mapdirection.util.RateLimiter
import com.google.android.gms.common.api.GoogleApiClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlaceRepository @Inject constructor(appExecutor: AppExecutor,
                                          placeDao: PlaceDao,
                                          placeService: PlaceService) {
    private val mAppExecutor = appExecutor
    private val mPlaceDao = placeDao
    private val mPlaceService = placeService

    private val mPlaceRateLimit = RateLimiter<String>(24, TimeUnit.HOURS)

    fun getPlaceById(googleApiClient: GoogleApiClient, placeId: String)
            : LiveData<Resource<GeoPlace>> {
        return object: NetworkBoundResource<GeoPlace, GeoPlace>(mAppExecutor) {
            override fun saveCallResult(item: GeoPlace) {
                mPlaceDao.insert(item)
            }

            override fun shouldFetch(data: GeoPlace?): Boolean {
                return data == null || mPlaceRateLimit.shouldFetch(data.placeId)
            }

            override fun loadFromDb(): LiveData<GeoPlace> {
                return mPlaceDao.getPlaceById(placeId)
            }

            override fun createCall(): LiveData<ApiResponse<GeoPlace>> {
                return mPlaceService.getPlace(googleApiClient, placeId)
            }
        }.asLiveData()
    }
}
