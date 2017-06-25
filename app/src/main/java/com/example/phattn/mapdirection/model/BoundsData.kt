package com.example.phattn.mapdirection.model

import com.squareup.moshi.Json

data class BoundsData(@Json(name = "northeast") val northeast: LocationData,
                      @Json(name = "southwest") val southwest: LocationData)
