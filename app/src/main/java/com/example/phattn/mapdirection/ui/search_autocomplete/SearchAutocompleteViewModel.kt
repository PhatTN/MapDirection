package com.example.phattn.mapdirection.ui.search_autocomplete

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.os.Bundle
import android.support.annotation.NonNull
import com.example.phattn.mapdirection.AppConstants
import com.example.phattn.mapdirection.model.Resource
import com.example.phattn.mapdirection.model.SearchPrediction
import com.example.phattn.mapdirection.repository.SearchHistoryRepository
import com.example.phattn.mapdirection.repository.SearchPredictionRepository
import com.example.phattn.mapdirection.util.DirectionUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber
import javax.inject.Inject

class SearchAutocompleteViewModel @Inject constructor(application: Application,
                                                      searchPredictionRepository: SearchPredictionRepository,
                                                      searchHistoryRepository: SearchHistoryRepository)
    : AndroidViewModel(application), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    companion object {
        const val DEFAULT_GET_HISTORIES_SIZE = 50
    }

    private val mQuery = MutableLiveData<String>()
    private var mResult : LiveData<Resource<List<SearchPrediction>>>
    private var mIsClientConnected = false
    private lateinit var mCenterLocation : LatLng

    private val mGoogleApiClient = GoogleApiClient.Builder(application, this, this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(Places.GEO_DATA_API)
            .build()
    private val mSearchPredictionRepository = searchPredictionRepository
    private val mSearchHistoryRepository = searchHistoryRepository

    private var mHistoryPage = 0

    init {
        mGoogleApiClient.connect()
        mResult = Transformations.switchMap(mQuery, { search ->
            when {
                // TODO handle load more histories
                search == null || search.isEmpty() -> mSearchHistoryRepository.getHistories(
                        mHistoryPage /* page */, DEFAULT_GET_HISTORIES_SIZE /* size */)
                else -> loadSearchPredictions(search)
            }
        })
    }

    fun loadSearchPredictions(query: String) : LiveData<Resource<List<SearchPrediction>>> {
        return mSearchPredictionRepository.getPredictions(
                mGoogleApiClient, query,
                DirectionUtil.calculateLatLngBounds(
                        mCenterLocation, AppConstants.DEFAULT_DISTANCE_LATLNG_BOUNDS),
                AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                        .setCountry(AppConstants.DIRECTION_FILTER_COUNTRY_VN)
                        .build())
    }

    fun setSearchQuery(@NonNull query: String) {
        mQuery.value = query
    }

    fun getResult() = mResult

    fun setCenterLocation(center: LatLng) { mCenterLocation = center }

    override fun onCleared() {
        mGoogleApiClient.disconnect()
        mIsClientConnected = false
        super.onCleared()
    }

    override fun onConnected(bundle: Bundle?) {
        mIsClientConnected = true
        Timber.d("GoogleApiClient is connected")
    }

    override fun onConnectionSuspended(cause: Int) {
        // TODO handle this case
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // TODO handle this case
    }

}
