package com.example.phattn.mapdirection.util

class DbQueryUtil {

    companion object {
        /**
         * Add '%' wildcard to query value. Because in present, @Dao annotation processing doesn't
         * support '%' wildcard. So, in order to support wildcard in query. We must add it to query
         * value itself
         */
        fun buildParamForSearch(query: String) = "$query%"
    }

}