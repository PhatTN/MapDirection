package com.example.phattn.mapdirection.ui.map

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class LocationLiveData(context: Context) : LiveData<Location>(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    companion object {
        val INTERVAL_UPDATES = 10000L // 10s
        val FASTEST_INTERVAL_UPDATES = 5000L // 5s
    }

    private val mGoogleApiClient: GoogleApiClient = GoogleApiClient.Builder(context, this, this)
            .addApi(LocationServices.API)
            .build()
    private val mLocationRequest: LocationRequest = LocationRequest()

    private var mCurrentLat = Double.NaN
    private var mCurrentLng = Double.NaN

    init {
        mLocationRequest.interval = INTERVAL_UPDATES
        mLocationRequest.fastestInterval = FASTEST_INTERVAL_UPDATES
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onActive() {
        // Wait for the GoogleApiClient to be connected
        mGoogleApiClient.connect()
    }

    override fun onInactive() {
        if (mGoogleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
        mGoogleApiClient.disconnect()
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        // Try to immediately find a location
        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        lastLocation?.let {
            if (shouldNotify(lastLocation)) {
                mCurrentLat = lastLocation.latitude
                mCurrentLng = lastLocation.longitude
                value = lastLocation
            }
        }

        // Request updates if there's someone observing
        if (hasActiveObservers()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this)
        }
    }

    override fun onConnectionSuspended(cause: Int) {
        // TODO handle this case
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // TODO exposing this state
    }

    override fun onLocationChanged(location: Location) {
        // Deliver the location changes
        if (shouldNotify(location)) {
            mCurrentLat = location.latitude
            mCurrentLng = location.longitude
            value = location
        }
    }

    private fun shouldNotify(location: Location)
            = location.latitude != mCurrentLat && location.longitude != mCurrentLng

}
