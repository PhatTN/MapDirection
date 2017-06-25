package com.example.phattn.mapdirection.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.support.annotation.WorkerThread
import com.example.phattn.mapdirection.AppExecutor
import com.example.phattn.mapdirection.api.ApiResponse
import com.example.phattn.mapdirection.model.Resource

/**
 * A generic class that can provide a resouce backed by both the sqlite database and the network
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>
 * @param <ResultType> Type for the Resource data
 * @param <RequestType> Type for the API response
 */
abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor(appExecutor: AppExecutor) {
    private val mAppExecutors = appExecutor
    private val mResult = MediatorLiveData<Resource<ResultType>>()

    init {
        mResult.value = Resource.loading(null)
        val dbSource = loadFromDb()
        mResult.addSource(dbSource, { data ->
            mResult.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                mResult.addSource(dbSource, { newData ->
                    mResult.value = Resource.success(newData)
                })
            }
        })
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        mResult.addSource(dbSource, { newData -> mResult.value = Resource.loading(newData) })
        mResult.addSource(apiResponse, { response ->
            mResult.removeSource(apiResponse)
            mResult.removeSource(dbSource)
            if (response != null && response.isSuccessful()) {
                mAppExecutors.diskIO().execute({
                    val result = processResponse(response)
                    result?.let { saveCallResult(result) }
                    mAppExecutors.mainThread().execute({
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        mResult.addSource(loadFromDb(), { newData -> mResult.value = Resource.success(newData) })
                    })
                })
            } else {
                onFetchFailed()
                mResult.addSource(dbSource, { newData ->
                    mResult.value = Resource.error(response!!.errorMessage()!!, newData)
                })
            }
        })
    }

    /**
     * Called when the fetch fails.
     */
    protected fun onFetchFailed() {}

    /**
     * returns a LiveData that represents the resource
     */
    fun asLiveData() : LiveData<Resource<ResultType>> {
        return mResult
    }

    @WorkerThread
    protected fun processResponse(response: ApiResponse<RequestType>) : RequestType? {
        return response.data()
    }

    /**
     * Called to save the result of the API response into the database
     */
    @WorkerThread
    protected abstract fun saveCallResult(@NonNull item: RequestType)

    /**
     * Called with the data in the database to decide whether it should be
     * fetched from the network.
     */
    @MainThread
    protected abstract fun shouldFetch(@Nullable data: ResultType?) : Boolean

    /**
     * Called to get the cached data from the database
     */
    @NonNull
    @MainThread
    protected abstract fun loadFromDb() : LiveData<ResultType>

    /**
     * Called to create the API call.
     */
    @NonNull
    @MainThread
    protected abstract fun createCall() : LiveData<ApiResponse<RequestType>>

}