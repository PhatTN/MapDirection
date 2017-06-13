package com.example.phattn.mapdirection.ui.map

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.location.Location

class MapsViewModel(application: Application) : AndroidViewModel(application) {

    private val locationLiveData: LocationLiveData = LocationLiveData(application)

    fun getLocation() : LiveData<Location> {
        return locationLiveData
    }
}