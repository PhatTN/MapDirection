package com.example.phattn.mapdirection.repository

import com.example.phattn.mapdirection.AppExecutor
import com.example.phattn.mapdirection.api.DirectionService
import com.example.phattn.mapdirection.extension.convertToString
import com.example.phattn.mapdirection.model.DirectionData
import com.example.phattn.mapdirection.model.Resource
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DirectionRepository @Inject constructor(appExecutor: AppExecutor,
                                              directionService: DirectionService) {

    private val mAppExecutor = appExecutor
    private val mDirectionService = directionService

    fun getDirection(source: LatLng, dest: LatLng, mode: String, units: String)
            : Observable<Resource<DirectionData>> {



        val result = Observable.create<Resource<DirectionData>> { emitter ->
            mDirectionService
                    .getDirections(source.convertToString(), dest.convertToString(), mode, units)
                    .observeOn(Schedulers.from(mAppExecutor.mainThread()))
                    .subscribeOn(Schedulers.from(mAppExecutor.networkIO()))
                    .subscribe({
                        emitter.onNext(Resource.success(it))
                        emitter.onComplete()
                    }, { error ->
                        emitter.onNext(Resource.error(error.message!!, null))
                    })
        }

        return result
    }

}