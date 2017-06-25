package com.example.phattn.mapdirection.util

import android.arch.lifecycle.LiveData

/**
 * A LiveData class that has {@code null} value
 */
class AbsentLiveData<T> private constructor(): LiveData<T>() {

    init {
        postValue(null)
    }

    companion object {
        fun <T> create() : LiveData<T> = AbsentLiveData()
    }
}