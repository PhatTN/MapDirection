package com.example.phattn.mapdirection.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.phattn.mapdirection.R
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import permissions.dispatcher.*
import timber.log.Timber

@RuntimePermissions
class MapsActivity : LifecycleActivity(), OnMapReadyCallback {

    companion object {
        const val PLACE_FROM_AUTO_COMPLETE_REQUEST = 101
        const val PLACE_TO_AUTO_COMPLETE_REQUEST = 102
        const val PLACE_RESTRICT_COUNTRY_VN = "VN"
    }

    private lateinit var mMap: GoogleMap

    // Binding views
    @BindView(R.id.textview_maps_source_place) lateinit var mTextViewSourcePlace : TextView
    @BindView(R.id.textview_maps_dest_place) lateinit var mTextViewDestPlace : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        ButterKnife.bind(this)

        MapsActivityPermissionsDispatcher.initializeMapWithCheck(this)
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun initializeMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.fragment_maps_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.isMyLocationEnabled = true

        val model = ViewModelProviders.of(this).get(MapsViewModel::class.java)
        model.getLocation().observe(this, Observer { location ->
            Timber.d("Location: ${location?.latitude}, ${location?.longitude}")
            location.let {
                val latLng = LatLng(location!!.latitude, location.longitude)
                moveCamera(latLng)
            }
        })
    }

    private fun moveCamera(latLng: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED || requestCode == PlaceAutocomplete.RESULT_ERROR) {
            return
        }
        when (requestCode) {
            PLACE_FROM_AUTO_COMPLETE_REQUEST -> {
                val place = PlaceAutocomplete.getPlace(this, data)
                mTextViewSourcePlace.text = place.name
            }
            PLACE_TO_AUTO_COMPLETE_REQUEST -> {
                val place = PlaceAutocomplete.getPlace(this, data)
                mTextViewDestPlace.text = place.name
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

    @OnClick(R.id.textview_maps_source_place)
    fun pickSourcePlace() {
        openSearchAutoComplete(PLACE_FROM_AUTO_COMPLETE_REQUEST)
    }

    @OnClick(R.id.textview_maps_dest_place)
    fun pickDestPlace() {
        openSearchAutoComplete(PLACE_TO_AUTO_COMPLETE_REQUEST)
    }

    private fun openSearchAutoComplete(requestCode: Int) {
        try {
            val typeFilter = AutocompleteFilter.Builder()
                    .setCountry(PLACE_RESTRICT_COUNTRY_VN)
                    .build()
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(this)
            startActivityForResult(intent, requestCode)
        } catch (e : GooglePlayServicesRepairableException) {
            // TODO handle this case
        } catch (e : GooglePlayServicesNotAvailableException) {
            // TODO handle this case
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