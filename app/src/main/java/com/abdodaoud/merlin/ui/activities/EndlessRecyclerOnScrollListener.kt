package com.abdodaoud.merlin.ui.activities

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

abstract class EndlessRecyclerOnScrollListener(val mLinearLayoutManager: LinearLayoutManager):
        RecyclerView.OnScrollListener() {

    var previousTotal = 0 // The total number of items in the dataset after the last load
    var loading = true // True if we are still waiting for the last set of data to load.
    val visibleThreshold = 5 // The minimum amount of items to have below your current scroll position before loading more.

    private var current_page = 1

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = recyclerView!!.childCount
        val totalItemCount = mLinearLayoutManager.itemCount
        val firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            // Do something
            current_page++
            onLoadMore(current_page)
            loading = true
        }
    }

    abstract fun onLoadMore(currentPage: Int)
}