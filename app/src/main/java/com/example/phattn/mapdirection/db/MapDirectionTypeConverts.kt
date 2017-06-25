package com.example.phattn.mapdirection.db

import android.arch.persistence.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class MapDirectionTypeConverts {
    companion object {

        @TypeConverter
        @JvmStatic
        fun latLngToString(data: LatLng) : String {
            return "${data.latitude},${data.longitude}"
        }

        @TypeConverter
        @JvmStatic
        fun stringToLatLng(data: String) : LatLng {
            val latLng = data.split(",")
            return LatLng(latLng[0].toDouble(), latLng[1].toDouble())
        }

        @TypeConverter
        @JvmStatic
        fun toDate(timestamp: Long) = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofTotalSeconds(0))

        @TypeConverter
        @JvmStatic
        fun toTimestamp(date: LocalDateTime) = date.toInstant(ZoneOffset.ofTotalSeconds(0)).epochSecond
    }
}