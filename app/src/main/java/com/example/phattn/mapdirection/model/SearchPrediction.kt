package com.example.phattn.mapdirection.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index

/**
 * SearchPrediction entity
 */
@Entity(indices = arrayOf(Index("full_text")),
        primaryKeys = arrayOf("place_id"), tableName = "search_prediction")
class SearchPrediction(@ColumnInfo(name = "place_id") val placeId: String,
                       @ColumnInfo(name = "full_text") val fullText: String,
                       @ColumnInfo(name = "primary_text") val primaryText: String,
                       @ColumnInfo(name = "secondary_text") val secondaryText: String)