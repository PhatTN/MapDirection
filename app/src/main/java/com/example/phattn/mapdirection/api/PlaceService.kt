package com.example.phattn.mapdirection.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.text.style.CharacterStyle
import com.example.phattn.mapdirection.AppExecutor
import com.example.phattn.mapdirection.model.GeoPlace
import com.example.phattn.mapdirection.model.SearchPrediction
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLngBounds
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceService @Inject constructor(appExecutor: AppExecutor) {

    private val mAppExecutor = appExecutor

    fun getPredictions(apiClient: GoogleApiClient, query: String, bounds: LatLngBounds,
                       filters: AutocompleteFilter)
            : LiveData<ApiResponse<List<SearchPrediction>>> {
        val result = MutableLiveData<ApiResponse<List<SearchPrediction>>>()
        val pendingResult = Places.GeoDataApi.getAutocompletePredictions(apiClient, query, bounds, filters)
        mAppExecutor.networkIO().execute {
            val predictionsBuffer = pendingResult.await(60, TimeUnit.SECONDS)

            when (predictionsBuffer.status.isSuccess) {
                false -> result.postValue(ApiResponse(Throwable(predictionsBuffer.status.statusMessage)))
                true -> {
                    val predictions = mutableListOf<SearchPrediction>()
                    val iterator = predictionsBuffer.iterator()

                    for (item in iterator) {
                        if (item.isDataValid) {
                            predictions.add(SearchPrediction(item.placeId!!,
                                    item.getFullText(CharacterStyle.wrap(null)).toString(),
                                    item.getPrimaryText(CharacterStyle.wrap(null)).toString(),
                                    item.getSecondaryText(CharacterStyle.wrap(null)).toString()))
                        }
                    }
                    predictionsBuffer.release()

                    result.postValue(ApiResponse(predictions))
                }
            }
        }

        return result
    }

    fun getPlace(apiClient: GoogleApiClient, placeId: String) : LiveData<ApiResponse<GeoPlace>> {
        val result = MutableLiveData<ApiResponse<GeoPlace>>()
        val pendingResult = Places.GeoDataApi.getPlaceById(apiClient, placeId)
        mAppExecutor.networkIO().execute {
            val placeBuffer = pendingResult.await(60, TimeUnit.SECONDS)

            when (placeBuffer.status.isSuccess) {
                false -> result.postValue(ApiResponse(Throwable(placeBuffer.status.statusMessage)))
                true -> {
                    if (placeBuffer.count > 0) {
                        val place = placeBuffer[0]
                        result.postValue(ApiResponse(GeoPlace(place.id, place.name.toString(),
                                place.address.toString(), place.phoneNumber.toString(),
                                place.websiteUri?.toString(), place.latLng)))
                    } else {
                        result.postValue(ApiResponse(
                                IllegalArgumentException("Provided placeId is not existing")))
                    }
                }
            }

            placeBuffer.release()
        }

        return result
    }

}