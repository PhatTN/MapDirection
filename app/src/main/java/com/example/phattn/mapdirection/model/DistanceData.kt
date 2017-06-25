package com.example.phattn.mapdirection.model

import com.squareup.moshi.Json

data class DistanceData(@Json(name = "text") val name: String, @Json(name = "value") val value: Int)
