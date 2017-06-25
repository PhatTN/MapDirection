package com.example.phattn.mapdirection.ui.map

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.location.Location
import android.os.Bundle
import com.example.phattn.mapdirection.AppConstants
import com.example.phattn.mapdirection.model.GeoPlace
import com.example.phattn.mapdirection.model.Resource
import com.example.phattn.mapdirection.model.Status
import com.example.phattn.mapdirection.repository.DirectionRepository
import com.example.phattn.mapdirection.repository.PlaceRepository
import com.example.phattn.mapdirection.repository.SearchHistoryRepository
import com.example.phattn.mapdirection.util.AbsentLiveData
import com.example.phattn.mapdirection.util.DirectionUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class MapsViewModel @Inject constructor(application: Application,
                                        placeRepository: PlaceRepository,
                                        directionRepository: DirectionRepository,
                                        searchHistoryRepository: SearchHistoryRepository)
    : ViewModel(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private val mLocationLiveData: LocationLiveData = LocationLiveData(application)
    val mSourcePlace : LiveData<Resource<GeoPlace>>
    val mDestPlace : LiveData<Resource<GeoPlace>>
    val mSourcePlaceId = MutableLiveData<String>()
    val mDestPlaceId = MutableLiveData<String>()
    val mPointsData = MutableLiveData<Resource<List<LatLng>>>()

    private var mIsClientConnected = false

    private val mGoogleApiClient = GoogleApiClient.Builder(application, this, this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(Places.GEO_DATA_API)
            .build()

    private val mPlaceRepository = placeRepository
    private val mDirectionRepository = directionRepository
    private val mSearchHistoryRepository = searchHistoryRepository

    init {
        mGoogleApiClient.connect()
        mSourcePlace = Transformations.switchMap(mSourcePlaceId, { placeId ->
            when {
                placeId == null || placeId.isEmpty() -> AbsentLiveData.create<Resource<GeoPlace>>()
                else -> {
                    mSearchHistoryRepository.saveSearchHistory(placeId)
                    mPlaceRepository.getPlaceById(mGoogleApiClient, placeId)
                }
            }
        })
        mDestPlace = Transformations.switchMap(mDestPlaceId, { placeId ->
            when {
                placeId == null || placeId.isEmpty() -> AbsentLiveData.create<Resource<GeoPlace>>()
                else -> {
                    mSearchHistoryRepository.saveSearchHistory(placeId)
                    mPlaceRepository.getPlaceById(mGoogleApiClient, placeId)
                }
            }
        })
    }

    override fun onCleared() {
        mGoogleApiClient.disconnect()
        mIsClientConnected = false
        super.onCleared()
    }

    override fun onConnected(p0: Bundle?) {
        mIsClientConnected = true
    }

    override fun onConnectionSuspended(p0: Int) {
        // TODO handle this case
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        // TODO handle this case
    }

    fun getLocation() : LiveData<Location> {
        return mLocationLiveData
    }

    fun getDirection() {
        if (mSourcePlace.value?.data?.latLng != null && mDestPlace.value?.data?.latLng != null) {
            mDirectionRepository.getDirection(mSourcePlace.value?.data?.latLng!!,
                    mDestPlace.value?.data?.latLng!!, AppConstants.DIRECTION_MODE_DRIVING,
                    AppConstants.DIRECTION_UNITS_METRIC)
                    .subscribe { (status, data, message) ->
                        when (status) {
                            Status.ERROR  -> mPointsData.value = Resource.error(message!!, null)
                            Status.LOADING -> mPointsData.value = Resource.loading(null)
                            Status.SUCCESS -> {
                                data?.let {
                                    when (data.status) {
                                        AppConstants.DIRECTION_STATUS_OK -> {
                                            val points = arrayListOf<LatLng>()
                                            val routes = data.routes
                                            routes.map {
                                                it.legs
                                            }.forEach { legs ->
                                                legs.map {
                                                    it.steps
                                                }.flatMap {
                                                    it
                                                }.forEach {
                                                    points.addAll(DirectionUtil.decodePolyline(it.polyline.points))
                                                }
                                            }
                                            mPointsData.value = Resource.success(points)
                                        }
                                        AppConstants.DIRECTION_STATUS_NOT_FOUND -> {
                                            // TODO handle not found case
                                        }
                                        AppConstants.DIRECTION_STATUS_ZERO_RESULTS -> {
                                            // TODO handle zero results case
                                        }
                                        AppConstants.DIRECTION_STATUS_MAX_WAYPOINTS_EXCEEDED -> {
                                            // TODO handle max waypoints case
                                        }
                                        AppConstants.DIRECTION_STATUS_MAX_ROUTE_LENGTH_EXCEEDED -> {
                                            // TODO handle route length exceeded case
                                        }
                                        AppConstants.DIRECTION_STATUS_INVALID_REQUEST -> {
                                            // TODO handle invalid request case
                                        }
                                        AppConstants.DIRECTION_STATUS_OVER_QUERY_LIMIT -> {
                                            // TODO handle over query limit
                                        }
                                        AppConstants.DIRECTION_STATUS_REQUEST_DENIED -> {
                                            // TODO handle request denied
                                        }
                                        AppConstants.DIRECTION_STATUS_UNKNOWN_ERROR -> {
                                            // TODO handle unknown error
                                        }
                                    }
                                }
                            }
                        }
                    }
        }
    }
}