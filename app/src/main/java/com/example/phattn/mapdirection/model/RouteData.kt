package com.example.phattn.mapdirection.model

import com.squareup.moshi.Json

data class RouteData(@Json(name = "bounds") val bounds: BoundsData,
                     @Json(name = "copyrights") val copyrights: String,
                     @Json(name = "legs") val legs: List<RouteLegData>,
                     @Json(name = "overview_polyline") val overviewPolyline: PolylineData,
                     @Json(name = "summary") val summary: String)