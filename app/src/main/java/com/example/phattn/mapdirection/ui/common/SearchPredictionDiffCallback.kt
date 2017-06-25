package com.example.phattn.mapdirection.ui.common

import android.support.v7.util.DiffUtil
import com.example.phattn.mapdirection.model.SearchPrediction

class SearchPredictionDiffCallback(oldList: List<SearchPrediction>?,
                                   newList: List<SearchPrediction>?) : DiffUtil.Callback() {

    private val mOldList = oldList
    private val mNewList = newList

    override fun getOldListSize() = mOldList?.size ?: 0

    override fun getNewListSize() = mNewList?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldPrediction = mOldList!![oldItemPosition]
        val newPrediction = mNewList!![newItemPosition]
        return oldPrediction.placeId == newPrediction.placeId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldPrediction = mOldList!![oldItemPosition]
        val newPrediction = mNewList!![newItemPosition]
        return oldPrediction.primaryText == newPrediction.primaryText
                && oldPrediction.secondaryText == newPrediction.secondaryText
    }

}