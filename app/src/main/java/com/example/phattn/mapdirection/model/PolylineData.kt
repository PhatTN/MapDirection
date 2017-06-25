package com.example.phattn.mapdirection.model

import com.squareup.moshi.Json

data class PolylineData(@Json(name = "points") val points: String)
