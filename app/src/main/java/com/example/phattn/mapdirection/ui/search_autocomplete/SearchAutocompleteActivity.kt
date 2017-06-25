package com.example.phattn.mapdirection.ui.search_autocomplete

import android.app.Activity
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnTextChanged
import com.example.phattn.mapdirection.R
import com.example.phattn.mapdirection.model.SearchPrediction
import com.example.phattn.mapdirection.model.Status
import com.example.phattn.mapdirection.ui.common.SearchPredictionAdapter
import com.example.phattn.mapdirection.util.ItemClickSupport
import com.google.android.gms.maps.model.LatLng
import dagger.android.AndroidInjection
import javax.inject.Inject

class SearchAutocompleteActivity : LifecycleActivity(), ItemClickSupport.OnItemClickListener {

    companion object {

        private const val PARAMS_CENTER_LOCATION = "PARAMS_CENTER_LOCATION"

        const val ARG_SELECTED_PLACE_ID = "ARG_SELECTED_PLACE_ID"
        const val ARG_SELECTED_PLACE_PRIMARY_TEXT = "ARG_SELECTED_PLACE_PRIMARY_TEXT"

        fun startActivityForResult(activity: Activity, requestCode: Int, centerLocation: LatLng) {
            val intent = Intent(activity, SearchAutocompleteActivity::class.java)
            intent.putExtra(PARAMS_CENTER_LOCATION, centerLocation)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    @Inject lateinit var viewModelFactory : ViewModelProvider.Factory
    private lateinit var mViewModel: SearchAutocompleteViewModel

    @BindView(R.id.searchautocomplete_recyclerview_searchpredictions)
    lateinit var mPredictionRecyclerView : RecyclerView
    @BindView(R.id.searchautocomplete_textview_searchhistories)
    lateinit var mHistorySearchTextView : TextView
    @BindView(R.id.searchautocomplete_lineseparator_searchhistories)
    lateinit var mHistorySearchLineSeparator : View

    private lateinit var mPredictionAdapter : SearchPredictionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_autocomplete)
        ButterKnife.bind(this)

        mViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(SearchAutocompleteViewModel::class.java)

        if (savedInstanceState == null) {
            val center = intent.getParcelableExtra<LatLng?>(PARAMS_CENTER_LOCATION)
                    ?: throw RuntimeException("Must provide a center location when start SearchAutocompleteActivity")
            mViewModel.setCenterLocation(center)
        }

        mViewModel.getResult().observe(this, Observer { predictions ->
            var updatedItems: List<SearchPrediction>? = null
            predictions?.let {
                when (predictions.status) {
                    Status.SUCCESS -> {
                        updatedItems = predictions.data
                    }
                    Status.LOADING -> {
                        // TODO handle loading case
                    }
                    Status.ERROR -> {
                        // TODO handle error case
                    }
                }
            }
            mPredictionAdapter.updatePredictionItems(updatedItems)
        })

        // Initialize recyclerview
        mPredictionAdapter = SearchPredictionAdapter(this, arrayListOf())
        mPredictionRecyclerView.adapter = mPredictionAdapter
        mPredictionRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        ItemClickSupport.addTo(mPredictionRecyclerView).setOnItemClickListener(this)


        mViewModel.setSearchQuery("")
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onItemClicked(recyclerView: RecyclerView, position: Int, view: View) {
        val prediction = mPredictionAdapter.getItemAt(position)
        val intent = Intent()
        if (prediction != null) {
            intent.putExtra(ARG_SELECTED_PLACE_ID, prediction.placeId)
            intent.putExtra(ARG_SELECTED_PLACE_PRIMARY_TEXT, prediction.primaryText)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    @OnTextChanged(value = R.id.searchautocomplete_edittext_search, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onSearchTextChanged(editable: Editable) {
        val isSearchHistoriesVisible = if (editable.isEmpty()) View.VISIBLE else View.GONE
        mHistorySearchTextView.visibility = isSearchHistoriesVisible
        mHistorySearchLineSeparator.visibility = isSearchHistoriesVisible
        mViewModel.setSearchQuery(editable.toString())
    }
}
