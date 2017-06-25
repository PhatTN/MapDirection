package com.example.phattn.mapdirection.api

import android.support.annotation.Nullable

/**
 * Common class used by API responses
 */
class ApiResponse<T> {
    private var mIsSuccessful: Boolean = false
    @Nullable private var mData: T? = null
    @Nullable private var mErrorMessage: String? = null

    constructor(error: Throwable) {
        mIsSuccessful = false
        mData = null
        mErrorMessage = error.message
    }

    constructor(data: T) {
        mIsSuccessful = true
        mData = data
        mErrorMessage = null
    }

    fun isSuccessful() = mIsSuccessful
    fun data() = mData
    fun errorMessage() = mErrorMessage
}