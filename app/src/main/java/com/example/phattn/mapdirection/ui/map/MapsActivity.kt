package com.example.phattn.mapdirection.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.phattn.mapdirection.R
import com.example.phattn.mapdirection.model.Status
import com.example.phattn.mapdirection.ui.search_autocomplete.SearchAutocompleteActivity
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.android.AndroidInjection
import permissions.dispatcher.*
import timber.log.Timber
import javax.inject.Inject

@RuntimePermissions
class MapsActivity : LifecycleActivity(), OnMapReadyCallback {

    companion object {
        const val AR_CODE_PLACE_SOURCE = 101
        const val AR_CODE_PLACE_DEST = 102
    }

    @Inject lateinit var viewModelFactory : ViewModelProvider.Factory

    private lateinit var mMap: GoogleMap
    private var mRoutePolyline: Polyline? = null

    private var mSourceMarker: Marker? = null
    private var mDestMarker: Marker? = null

    private var mSourceMarkerOptions: MarkerOptions? = null
    private var mDestMarkerOptions: MarkerOptions? = null

    private lateinit var mViewModel : MapsViewModel

    // Binding views
    @BindView(R.id.maps_textview_source_place) lateinit var mSourcePlaceTextView: TextView
    @BindView(R.id.maps_textview_dest_place) lateinit var mDestPlaceTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        ButterKnife.bind(this)
        AndroidInjection.inject(this)

        MapsActivityPermissionsDispatcher.initializeMapWithCheck(this)
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun initializeMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.maps_fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.isMyLocationEnabled = true

        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(MapsViewModel::class.java)
        mViewModel.getLocation().observe(this, object: Observer<Location> {
            override fun onChanged(location: Location?) {
                Timber.d("Location: ${location?.latitude}, ${location?.longitude}")
                Timber.d("onChange: Source PlaceId: ${mViewModel.mSourcePlaceId.value}")
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    moveCamera(latLng)
                    mViewModel.getLocation().removeObserver(this)
                }
            }
        })

        mViewModel.mSourcePlace.observe(this, Observer { sourcePlace ->
            sourcePlace?.let {
                when (sourcePlace.status) {
                    Status.SUCCESS -> {
                        Timber.d("Source placeId: ${sourcePlace.data?.latLng}")
                        sourcePlace.data?.let {
                            addSourceMarker(sourcePlace.data.latLng)
                            moveCamera(sourcePlace.data.latLng)
                            mSourcePlaceTextView.text = sourcePlace.data.name
                        }
                        mViewModel.getDirection()
                    }
                    Status.LOADING -> {
                        // TODO handle loading case
                    }
                    Status.ERROR -> {
                        // TODO handle error case
                    }
                }
            }
        })

        mViewModel.mDestPlace.observe(this, Observer { destPlace ->
            destPlace?.let {
                when (destPlace.status) {
                    Status.SUCCESS -> {
                        Timber.d("Dest placeId: ${destPlace.data?.latLng}")
                        destPlace.data?.let {
                            addDestMarker(destPlace.data.latLng)
                            moveCamera(destPlace.data.latLng)
                            mDestPlaceTextView.text = destPlace.data.name
                        }
                        mViewModel.getDirection()
                    }
                    Status.LOADING -> {
                        // TODO handle loading case
                    }
                    Status.ERROR -> {
                        // TODO handle error case
                    }
                }
            }
        })

        mViewModel.mPointsData.observe(this, Observer { points ->
            points?.let {
                when (points.status) {
                    Status.SUCCESS -> {
                        mRoutePolyline?.remove()
                        val lineOptions = PolylineOptions()
                        lineOptions.addAll(points.data)
                        lineOptions.width(12f /* line width */)
                        lineOptions.color(ContextCompat.getColor(this, R.color.colorMain))
                        lineOptions.geodesic(true)

                        // Drawing polyline on the Google Maps
                        mRoutePolyline = mMap.addPolyline(lineOptions)

                        val builder = LatLngBounds.Builder()
                        for (latLng in points.data!!) {
                            builder.include(latLng)
                        }

                        zoomRoute(builder.build(), 100 /* routePadding */)
                    }
                    Status.LOADING -> {
                        // TODO handle loading case
                    }
                    Status.ERROR -> {
                        // TODO handle error case
                    }
                }
            }
        })
    }

    private fun moveCamera(latLng: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f /* zoom level */))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f /* zoom level */), 2000 /* duration */, null)
    }

    private fun zoomRoute(latLngBounds: LatLngBounds, routePadding: Int) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding))
    }

    private fun addSourceMarker(latLng: LatLng) {
//        mSourceMarker?.let { mSourceMarker!!.remove() }
        if (mSourceMarkerOptions == null) {
            mSourceMarkerOptions = MarkerOptions()
            mSourceMarkerOptions!!.position(latLng)
            mSourceMarkerOptions!!.icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_RED))
            mSourceMarker = mMap.addMarker(mSourceMarkerOptions)
        } else {
            mSourceMarker!!.position = latLng
        }
    }

    private fun addDestMarker(latLng: LatLng) {
        if (mDestMarkerOptions == null) {
            mDestMarkerOptions = MarkerOptions()
            mDestMarkerOptions!!.position(latLng)
            mDestMarkerOptions!!.icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_BLUE))
            mDestMarker = mMap.addMarker(mDestMarkerOptions)
        } else {
            mDestMarker!!.position = latLng
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED || requestCode == PlaceAutocomplete.RESULT_ERROR) {
            return
        }
        when (requestCode) {
            AR_CODE_PLACE_SOURCE -> {
                mViewModel.mSourcePlaceId.value = data?.getStringExtra(
                        SearchAutocompleteActivity.ARG_SELECTED_PLACE_ID)
                mSourcePlaceTextView.text = data?.getStringExtra(
                        SearchAutocompleteActivity.ARG_SELECTED_PLACE_PRIMARY_TEXT)
            }
            AR_CODE_PLACE_DEST -> {
                mViewModel.mDestPlaceId.value = data?.getStringExtra(
                        SearchAutocompleteActivity.ARG_SELECTED_PLACE_ID)
                mDestPlaceTextView.text = data?.getStringExtra(
                        SearchAutocompleteActivity.ARG_SELECTED_PLACE_PRIMARY_TEXT)
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        MapsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    @OnClick(R.id.maps_textview_source_place)
    fun pickSourcePlace() {
        openSearchAutoComplete(AR_CODE_PLACE_SOURCE)
    }

    @OnClick(R.id.maps_textview_dest_place)
    fun pickDestPlace() {
        openSearchAutoComplete(AR_CODE_PLACE_DEST)
    }

    private fun openSearchAutoComplete(requestCode: Int) {
        if (mViewModel.getLocation().value != null) {
            SearchAutocompleteActivity.startActivityForResult(this, requestCode,
                    LatLng(mViewModel.getLocation().value!!.latitude,
                            mViewModel.getLocation().value!!.longitude))
        } else {
            Timber.d("Current location is not available. So you can't use this function")
        }
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun showRationaleForLocation(request: PermissionRequest) {
        AlertDialog.Builder(this)
                .setMessage(R.string.all_message_ask_location_permission)
                .setPositiveButton(R.string.all_button_allow, { _, _ -> request.proceed() })
                .setNegativeButton(R.string.all_button_deny, { _, _ -> request.cancel() })
                .show()
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun showDeniedForLocation() {
        Toast.makeText(this, R.string.all_message_location_permission_denied, Toast.LENGTH_SHORT).show()
    }
}