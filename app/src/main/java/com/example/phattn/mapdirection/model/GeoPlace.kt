package com.example.phattn.mapdirection.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import com.google.android.gms.maps.model.LatLng

/**
 * Place entity
 */
@Entity(indices = arrayOf(Index("id"), Index("name")),
        primaryKeys = arrayOf("id"), tableName = "place")
class GeoPlace(@ColumnInfo(name = "id") val placeId: String,
               @ColumnInfo(name ="name") val name: String,
               @ColumnInfo(name = "address") val address: String,
               @ColumnInfo(name = "phone_number") val phoneNumber: String?,
               @ColumnInfo(name = "website_uri") val websiteUri: String?,
               @ColumnInfo(name ="latlng") val latLng: LatLng)