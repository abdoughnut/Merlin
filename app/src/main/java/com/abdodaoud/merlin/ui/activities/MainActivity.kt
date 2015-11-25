package com.abdodaoud.merlin.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.abdodaoud.merlin.R
import com.abdodaoud.merlin.domain.commands.RequestDayFactCommand
import com.abdodaoud.merlin.domain.commands.RequestFactCommand
import com.abdodaoud.merlin.ui.adapters.FactListAdapter
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.Iconable
import kotlinx.android.synthetic.activity_main.*
import org.jetbrains.anko.async
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), ToolbarManager {

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    override val appBarLayout by lazy { find<AppBarLayout>(R.id.appBarLayout) }
    override val swipeRefreshLayout by lazy { find<SwipeRefreshLayout>(R.id.contentView) }
    override val density by lazy { resources.displayMetrics.density }
    val splashScreenImageView by lazy { find<ImageView>(R.id.splash_screen) }
    var notificationOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbarTitle = getString(R.string.app_name)

        setupNavDrawer()

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            loadFacts()
            swipeRefreshLayout.isRefreshing = false
        }

        factList.layoutManager = LinearLayoutManager(this)
        attachToScroll(factList)
    }

    override fun onPause() {
        super.onPause()
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
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(dayResult.url)))
                    }
                }
            }
            // TODO Fix the way data is retrieved to minimize data consumption
            factList.adapter = adapter
            splashScreenImageView.visibility = View.GONE
        }
    }

    private fun setupNavDrawer() {
        DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(AccountHeaderBuilder()
                        .withActivity(this)
                        .withHeaderBackground(R.drawable.nav_header_background)
                        .build())
                .addDrawerItems(
                        SecondaryDrawerItem().withName(R.string.nav_settings_title),
                        PrimaryDrawerItem().withName(R.string.nav_notifications).withIcon(
                                if (notificationOn) R.mipmap.nav_switch_on else R.mipmap.nav_switch_off),
                        PrimaryDrawerItem().withName(R.string.nav_time).withIcon(R.mipmap.nav_time),
                        PrimaryDrawerItem().withName(R.string.nav_theme).withIcon(R.mipmap.nav_theme),
                        DividerDrawerItem(),
                        SecondaryDrawerItem().withName(R.string.nav_about_title),
                        PrimaryDrawerItem().withName(R.string.nav_contact).withIcon(R.mipmap.nav_contact),
                        PrimaryDrawerItem().withName(R.string.nav_website).withIcon(R.mipmap.nav_website),
                        PrimaryDrawerItem().withName(R.string.nav_reddit).withIcon(R.mipmap.nav_reddit),
                        DividerDrawerItem(),
                        SecondaryDrawerItem().withName(R.string.nav_donate_title),
                        PrimaryDrawerItem().withName(R.string.nav_donate_small).withIcon(R.mipmap.nav_donate_small),
                        PrimaryDrawerItem().withName(R.string.nav_donate_medium).withIcon(R.mipmap.nav_donate_medium),
                        PrimaryDrawerItem().withName(R.string.nav_donate_large).withIcon(R.mipmap.nav_donate_large)
                )
                .withOnDrawerItemClickListener({ view, position, drawerItem ->
                        if (drawerItem != null) {
                            handleItemClick(position, drawerItem)
                        } else {
                            false
                        }
                    })
                .withFooter(R.layout.nav_footer)
                .withFooterDivider(false)
                .build()
    }

    private fun handleItemClick(position: Int, drawerItem : IDrawerItem<*>): Boolean {
        when (position) {
            // headers
            0, 1, 6, 11 -> {
                drawerItem.withSelectable(false)
                drawerItem.withSetSelected(false)
            }
            // notification
            2 -> {
                if (drawerItem is Iconable<*>) {
                    // TODO Set up notification handler
                    if (notificationOn) {
                        drawerItem.withIcon(getDrawable(R.mipmap.nav_switch_off))
                        notificationOn = false
                    } else {
                        drawerItem.withIcon(getDrawable(R.mipmap.nav_switch_on))
                        notificationOn = true
                    }
                }
            }
            // notification time
            3 -> {
                // TODO Set up notification time handler
                if (notificationOn) {
                    Log.d("DRAWER", "MUST SHOW TIME DIALOG")
                }
            }
            // theme
            4 -> {
                // TODO Set up theme handler
                Log.d("DRAWER", "MUST SHOW THEME DIALOG")
            }
            // contact
            7 -> {
                // TODO create gmail account for merlin
                val emailIntent = Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", "abdo@daoud.co", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Merlin The App");
                startActivity(emailIntent)
            }
            // visit website
            // TODO Set up website
            8 -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://abdodaoud.com/merlin")))

            // read more from Reddit
            9 -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.reddit.com/r/todayilearned/")))
            // donate
            12, 13, 14 -> {
                // TODO Set up in app billing handler
                Log.d("DRAWER", "MUST SHOW IN APP DONATION DIALOG")
            }
        }
        return true
    }
}