package com.example.phattn.mapdirection.model

import com.squareup.moshi.Json

data class DirectionData(@Json(name = "geocoded_waypoints") val geoCodedWayPoints: List<WayPointData>,
                         @Json(name = "routes") val routes: List<RouteData>,
                         @Json(name = "status") val status: String)
