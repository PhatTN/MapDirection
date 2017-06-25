package com.example.phattn.mapdirection.api

import com.example.phattn.mapdirection.AppConstants
import com.example.phattn.mapdirection.model.DirectionData
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST API access points for Google Maps Direction Service
 */
interface DirectionService {

    @GET("directions/json")
    fun getDirections(@Query("origin") origin: String,
                      @Query("destination") destination: String,
                      @Query("mode") mode: String,
                      @Query("units") units: String) : Observable<DirectionData>

    class Creator {
        companion object {
            fun newDirectionService() : DirectionService {
                val retrofit = Retrofit.Builder()
                        .baseUrl(AppConstants.GOOGLE_MAP_API)
                        .addConverterFactory(MoshiConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(OkHttpClient.Builder()
                                .addNetworkInterceptor(StethoInterceptor())
                                .build())
                        .build()
                return retrofit.create(DirectionService::class.java)
            }
        }
    }

}