package com.abdodaoud.merlin.ui.activities

import android.support.design.widget.AppBarLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import com.abdodaoud.merlin.extensions.ctx
import com.abdodaoud.merlin.extensions.slideEnter
import com.abdodaoud.merlin.extensions.slideExit

interface ToolbarManager {

    val toolbar: Toolbar
    val appBarLayout : AppBarLayout
    val swipeRefreshLayout : SwipeRefreshLayout
    val density : Float

    var toolbarTitle: String
        get() = toolbar.title.toString()
        set(value) {
            toolbar.title = value
        }

    fun enableHomeAsUp(up: () -> Unit) {
        toolbar.navigationIcon = createUpDrawable()
        toolbar.setNavigationOnClickListener { up() }
    }

    private fun createUpDrawable() = DrawerArrowDrawable(toolbar.ctx).apply { progress = 1f }

    fun attachToScroll(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        recyclerView.addOnScrollListener(object : EndlessRecyclerOnScrollListener(layoutManager) {

            override fun onLoadMore(currentPage: Int) {
                // do something...
                Log.i("AAAAAA", "HEELLOOOO " + currentPage)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val end = (64 * density) + toolbar.height.toFloat()
                swipeRefreshLayout.setProgressViewEndTarget(false, end.toInt())

                if (dy > 0) {
                    appBarLayout.slideExit()
                } else {
                    appBarLayout.slideEnter()
                }
            }
        })
    }
}