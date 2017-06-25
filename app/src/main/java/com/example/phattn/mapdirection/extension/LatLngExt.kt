package com.example.phattn.mapdirection.extension

import com.google.android.gms.maps.model.LatLng

fun LatLng.convertToString() = "${this.latitude},${this.longitude}"
