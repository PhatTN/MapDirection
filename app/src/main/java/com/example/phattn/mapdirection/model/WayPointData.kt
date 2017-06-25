package com.example.phattn.mapdirection.model

import com.squareup.moshi.Json

data class WayPointData(@Json(name = "geocoder_status") val geoCoderStatus: String,
                        @Json(name = "place_id") val placeId: String,
                        @Json(name = "types") val types: List<String>)