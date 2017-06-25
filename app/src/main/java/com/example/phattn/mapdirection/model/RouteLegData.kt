package com.example.phattn.mapdirection.model

import com.squareup.moshi.Json

data class RouteLegData(@Json(name = "distance") val distance: DistanceData,
                        @Json(name = "duration") val duration: DurationData,
                        @Json(name = "start_address") val startAddress: String,
                        @Json(name = "end_address") val endAddress: String,
                        @Json(name = "start_location") val startLocation: LocationData,
                        @Json(name = "end_location") val endLocation: LocationData,
                        @Json(name = "steps") val steps: List<RouteStepData>)
