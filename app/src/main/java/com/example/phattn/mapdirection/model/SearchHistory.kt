package com.example.phattn.mapdirection.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import org.threeten.bp.LocalDateTime

@Entity(primaryKeys = arrayOf("place_id"), tableName = "search_history")
data class SearchHistory(
        @ColumnInfo(name = "place_id") val placeId: String,
        @ColumnInfo(name = "search_time") val searchTime: LocalDateTime)
