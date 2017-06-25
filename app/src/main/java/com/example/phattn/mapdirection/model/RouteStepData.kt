package com.example.phattn.mapdirection.model

import com.squareup.moshi.Json

data class RouteStepData(
        @Json(name = "distance") val distance: DistanceData,
        @Json(name = "duration") val duration: DurationData,
        @Json(name = "start_location") val startLocation: LocationData,
        @Json(name = "end_location") val endLocation: LocationData,
        @Json(name = "html_instructions") val htmlInstruction: String,
        @Json(name = "polyline") val polyline: PolylineData,
        @Json(name = "travel_mode") val travelMode: String)