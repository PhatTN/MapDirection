package com.example.phattn.mapdirection.ui.common

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.ButterKnife
import com.example.phattn.mapdirection.R
import com.example.phattn.mapdirection.model.SearchPrediction

class SearchPredictionAdapter(context: Context, predictions: MutableList<SearchPrediction>?) : RecyclerView.Adapter<SearchPredictionAdapter.ViewHolder>() {

    private val mContext = context
    private val mSearchPredictions = predictions

    override fun getItemCount() = mSearchPredictions?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_search_prediction,
                parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val prediction = mSearchPredictions!![position]
        holder?.primaryText?.text = prediction.primaryText
        holder?.secondaryText?.text = prediction.secondaryText
    }

    fun updatePredictionItems(updatedItems: List<SearchPrediction>?) {
        val diffCallback = SearchPredictionDiffCallback(mSearchPredictions, updatedItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        mSearchPredictions?.let {
            mSearchPredictions.clear()
            updatedItems?.let { mSearchPredictions.addAll(updatedItems) }
        }
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemAt(position: Int) : SearchPrediction? {
        if (mSearchPredictions == null || position < 0 || position >= mSearchPredictions.size) {
            return null
        }

        return mSearchPredictions[position]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val primaryText: TextView
                = ButterKnife.findById(itemView, R.id.apdateritem_searchprediction_primarytext)
        val secondaryText: TextView
                = ButterKnife.findById(itemView, R.id.apdateritem_searchprediction_secondarytext)

    }
}