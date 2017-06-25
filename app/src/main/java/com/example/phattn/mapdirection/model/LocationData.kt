package com.example.phattn.mapdirection.model

import com.squareup.moshi.Json

data class LocationData (@Json(name = "lat") val lat: Double, @Json(name = "lng") val lng: Double)