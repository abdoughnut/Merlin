package com.abdodaoud.merlin.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.abdodaoud.merlin.R
import com.abdodaoud.merlin.domain.commands.RequestDayFactCommand
import com.abdodaoud.merlin.domain.commands.RequestFactCommand
import com.abdodaoud.merlin.ui.adapters.FactListAdapter
import com.mikepenz.materialdrawer.DrawerBuilder
import kotlinx.android.synthetic.activity_main.*
import org.jetbrains.anko.async
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), ToolbarManager {

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    override val appBarLayout by lazy { find<AppBarLayout>(R.id.appBarLayout) }
    override val swipeRefreshLayout by lazy { find<SwipeRefreshLayout>(R.id.contentView) }
    override val density by lazy { resources.displayMetrics.density }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbarTitle = getString(R.string.app_name);

        DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .build()

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            loadFacts()
            swipeRefreshLayout.isRefreshing = false
        }

        factList.layoutManager = LinearLayoutManager(this)
        attachToScroll(factList)
    }

    override fun onPause() {
        super.onPause();
    }

    override fun onResume() {
        super.onResume()
        loadFacts()
    }

    private fun loadFacts() = async {
        val result = RequestFactCommand().execute()
        uiThread {
            val adapter = FactListAdapter(result) {
                async {
                    val dayResult = RequestDayFactCommand(it.id).execute()
                    uiThread {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dayResult.url))
                        startActivity(intent)
                    }
                }
            }
            factList.adapter = adapter
        }
    }
}