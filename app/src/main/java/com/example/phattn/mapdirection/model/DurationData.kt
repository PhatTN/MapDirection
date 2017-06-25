package com.example.phattn.mapdirection.model

import com.squareup.moshi.Json

data class DurationData(@Json(name = "text") val text: String, @Json(name = "value") val value: Int)
