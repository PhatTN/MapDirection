package com.example.phattn.mapdirection.util

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil

class DirectionUtil {
    companion object {
        fun decodePolyline(polyline: String) : List<LatLng> {
            return PolyUtil.decode(polyline)
        }

        fun calculateLatLngBounds(center: LatLng, distance: Double) : LatLngBounds {
            val southwest = SphericalUtil.computeOffset(center, distance * Math.sqrt(2.0), 255.0)
            val northwest = SphericalUtil.computeOffset(center, distance * Math.sqrt(2.0), 45.0)

            return LatLngBounds(southwest, northwest)
        }
    }
}