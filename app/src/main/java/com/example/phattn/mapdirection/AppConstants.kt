package com.example.phattn.mapdirection

class AppConstants {

    companion object {
        const val GOOGLE_MAP_API = "https://maps.googleapis.com/maps/api/"
        const val DATABASE_FILENAME = "map.db"

        // TODO convert these mode constant to Class static field for easy maintainable and understandable
        /* Direction Travel Mode */
        const val DIRECTION_MODE_DRIVING = "driving"
        const val DIRECTION_MODE_WALKING = "walking"
        const val DIRECTION_MODE_BICYCLING = "bicycling"
        const val DIRECTION_MODE_TRANSIT = "transit"

        // TODO convert these unit constant to Class static field for easy maintainable and understandable
        /* Direction Units */
        const val DIRECTION_UNITS_METRIC = "metric"
        const val DIRECTION_UNITS_IMPERIAL = "imperial"

        // TODO convert these status code constant to Class static field for easy maintainable and understandable
        /* Direction Status Code */
        const val DIRECTION_STATUS_OK = "OK"
        const val DIRECTION_STATUS_NOT_FOUND = "NOT_FOUND"
        const val DIRECTION_STATUS_ZERO_RESULTS = "ZERO_RESULTS"
        const val DIRECTION_STATUS_MAX_WAYPOINTS_EXCEEDED = "MAX_WAYPOINTS_EXCEEDED"
        const val DIRECTION_STATUS_MAX_ROUTE_LENGTH_EXCEEDED = "MAX_ROUTE_LENGTH_EXCEEDED"
        const val DIRECTION_STATUS_INVALID_REQUEST = "INVALID_REQUEST"
        const val DIRECTION_STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT"
        const val DIRECTION_STATUS_REQUEST_DENIED = "REQUEST_DENIED"
        const val DIRECTION_STATUS_UNKNOWN_ERROR = "UNKNOWN_ERROR"

        // TODO convert these country code constant to Class static field for easy maintainable and understandable
        /* Direction Country code */
        const val DIRECTION_FILTER_COUNTRY_VN = "VN"

        // default distance using for creating LatLngBounds
        const val DEFAULT_DISTANCE_LATLNG_BOUNDS = 100.0

    }

}