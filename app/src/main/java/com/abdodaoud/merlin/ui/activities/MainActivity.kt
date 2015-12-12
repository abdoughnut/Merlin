package com.abdodaoud.merlin.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import com.abdodaoud.merlin.R
import com.abdodaoud.merlin.domain.commands.RequestFactCommand
import com.abdodaoud.merlin.extensions.maxDate
import com.abdodaoud.merlin.extensions.zeroedTime
import com.abdodaoud.merlin.ui.adapters.FactListAdapter
import com.abdodaoud.merlin.util.AlarmService
import com.abdodaoud.merlin.util.Constants
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.dd.CircularProgressButton
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.Iconable
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.activity_main.*
import me.mvdw.recyclerviewmergeadapter.adapter.RecyclerViewMergeAdapter
import org.jetbrains.anko.async
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), ToolbarManager, TimePickerDialog.OnTimeSetListener,
        BillingProcessor.IBillingHandler {

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    override val appBarLayout by lazy { find<AppBarLayout>(R.id.appBarLayout) }
    override val swipeRefreshLayout by lazy { find<SwipeRefreshLayout>(R.id.contentView) }
    override val density by lazy { resources.displayMetrics.density }

    val splashScreenImageView by lazy { find<ImageView>(R.id.splash_screen) }
    val retryButton by lazy { find<CircularProgressButton>(R.id.retry_button) }

    val bp by lazy { BillingProcessor(this, Constants.LICENSE_KEY, Constants.MERCHANT_ID, this) }

    var sharedPref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    var notificationOn: Boolean = false
    var hourOfDay: Int = 9
    var minute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbarTitle = getString(R.string.app_name)

        sharedPref = getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
        editor = sharedPref?.edit()

        notificationOn = sharedPref?.getBoolean(getString(R.string.pref_notification), false) as Boolean
        hourOfDay = sharedPref?.getInt(getString(R.string.pref_hour), 9) as Int
        minute = sharedPref?.getInt(getString(R.string.pref_minute), 0) as Int

        retryButton.isIndeterminateProgressMode = true
        retryButton.setOnClickListener {
            if (retryButton.progress == 0 || retryButton.progress == 100 ||
                    retryButton.progress == -1) loadFacts()
        }

        loadFacts()
        setupNavDrawer()

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            checkForNewFacts()
            swipeRefreshLayout.isRefreshing = false
        }

        val linearLayoutManager = LinearLayoutManager(this)
        factList.layoutManager = linearLayoutManager

        factList.addOnScrollListener(object: EndlessRecyclerOnScrollListener(linearLayoutManager) {
            override fun onLoadMore(currentPage: Int) {
                loadFacts(currentPage)
            }
        })

        attachToScroll(factList)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        checkForNewFacts()
    }

    override fun onDestroy() {
        bp.release()
        super.onDestroy()
    }

    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, second: Int) {
        this.hourOfDay = hourOfDay
        this.minute = minute

        editor?.putInt(getString(R.string.pref_hour), hourOfDay)
        editor?.putInt(getString(R.string.pref_minute), minute)
        editor?.commit()

        AlarmService(this).startAlarm()
    }

    override fun onBillingInitialized() {
        // Called when BillingProcessor was initialized and it's ready to purchase
    }

    override fun onProductPurchased(productId: String?, details: TransactionDetails?) {
        // Called when requested PRODUCT ID was successfully purchased
        bp.consumePurchase(productId)
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    override fun onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadFacts(currentPage: Int = 1) {
        retryButton.progress = 50
        val lastDate = System.currentTimeMillis().zeroedTime().maxDate(currentPage)
        try {
            _loadFacts(currentPage, lastDate).get()
        } catch (e: Exception) {
            retryButton.progress = -1
        }
    }

    private fun _loadFacts(currentPage: Int, lastDate: Long) = async {
        val result = RequestFactCommand().execute(currentPage, lastDate)
        uiThread {
            val adapter = FactListAdapter(result)
            retryButton.visibility = View.GONE
            if (factList.adapter != null) {
                if (factList.adapter is RecyclerViewMergeAdapter<*>) {
                    (factList.adapter as RecyclerViewMergeAdapter<FactListAdapter>)
                            .addAdapter(adapter)
                } else {
                    val mergeAdapter = RecyclerViewMergeAdapter<FactListAdapter>()
                    mergeAdapter.addAdapter(factList.adapter as FactListAdapter)
                    mergeAdapter.addAdapter(adapter)
                    factList.adapter = mergeAdapter
                }
            } else {
                factList.adapter = adapter
            }
            splashScreenImageView.visibility = View.GONE
        }
    }

    private fun checkForNewFacts() {
        if (factList.adapter != null) {
            async {
                val fact = if (factList.adapter is RecyclerViewMergeAdapter<*>)
                    ((factList.adapter as RecyclerViewMergeAdapter<FactListAdapter>)
                            .getSubAdapter(0) as FactListAdapter).getItemAtPosition(0)
                else (factList.adapter as FactListAdapter).getItemAtPosition(0)

                val result = RequestFactCommand().execute(-1, fact.created)
                uiThread {
                    val adapter = FactListAdapter(result)
                    if (factList.adapter != null) {
                        if (factList.adapter is RecyclerViewMergeAdapter<*>) {
                            val mergeAdapter = RecyclerViewMergeAdapter<FactListAdapter>()
                            mergeAdapter.addAdapter(adapter)
                            var count = 0
                            val listCount = (factList.adapter as RecyclerViewMergeAdapter<FactListAdapter>)
                                    .subAdapterCount
                            while (count < listCount) {
                                mergeAdapter.addAdapter((factList.adapter
                                        as RecyclerViewMergeAdapter<FactListAdapter>).getSubAdapter(count))
                                count++
                            }
                        } else {
                            val mergeAdapter = RecyclerViewMergeAdapter<FactListAdapter>()
                            mergeAdapter.addAdapter(adapter)
                            mergeAdapter.addAdapter(factList.adapter as FactListAdapter)
                            factList.adapter = mergeAdapter
                        }
                    } else {
                        factList.adapter = adapter
                    }
                }
            }
        } else {
            splashScreenImageView.visibility = View.VISIBLE
            retryButton.visibility = View.VISIBLE
            loadFacts()
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
                        PrimaryDrawerItem().withName(R.string.nav_notifications)
                                .withIcon(if (notificationOn) R.mipmap.nav_switch_on
                                else R.mipmap.nav_switch_off),
                        PrimaryDrawerItem().withName(R.string.nav_time).withIcon(R.mipmap.nav_time),
                        DividerDrawerItem(),
                        SecondaryDrawerItem().withName(R.string.nav_about_title),
                        PrimaryDrawerItem().withName(R.string.nav_contact)
                                .withIcon(R.mipmap.nav_contact),
                        PrimaryDrawerItem().withName(R.string.nav_website)
                                .withIcon(R.mipmap.nav_website),
                        PrimaryDrawerItem().withName(R.string.nav_reddit)
                                .withIcon(R.mipmap.nav_reddit),
                        DividerDrawerItem(),
                        SecondaryDrawerItem().withName(R.string.nav_donate_title),
                        PrimaryDrawerItem().withName(R.string.nav_donate_small)
                                .withIcon(R.mipmap.nav_donate_small),
                        PrimaryDrawerItem().withName(R.string.nav_donate_medium)
                                .withIcon(R.mipmap.nav_donate_medium),
                        PrimaryDrawerItem().withName(R.string.nav_donate_large)
                                .withIcon(R.mipmap.nav_donate_large)
                )
                .withOnDrawerItemClickListener({ view, position, drawerItem ->
                        if (drawerItem != null) handleItemClick(position, drawerItem) else false
                    })
                .withFooter(R.layout.nav_footer)
                .withFooterDivider(false)
                .build()
    }

    private fun handleItemClick(position: Int, drawerItem : IDrawerItem<*>): Boolean {
        when (position) {
            // headers
            0, 1, 5, 10 -> {
                drawerItem.withSelectable(false)
                drawerItem.withSetSelected(false)
            }
            // notification
            2 -> {
                if (drawerItem is Iconable<*>) {
                    if (notificationOn) {
                        drawerItem.withIcon(ContextCompat.getDrawable(this, R.mipmap.nav_switch_off))
                        notificationOn = false
                    } else {
                        drawerItem.withIcon(ContextCompat.getDrawable(this, R.mipmap.nav_switch_on))
                        notificationOn = true
                        AlarmService(this).startAlarm()
                    }

                    editor?.putBoolean(getString(R.string.pref_notification), notificationOn)
                    editor?.commit()
                }
            }
            // notification time
            3 -> {
                if (notificationOn) {
                    val tpd = TimePickerDialog.newInstance(this, hourOfDay, minute, false)
                    tpd.accentColor = R.color.colorPrimaryLight
                    tpd.show(fragmentManager, "AlarmTimeDialog")
                }
            }
            // contact
            6 -> startActivity(Intent(Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", Constants.EMAIL, null)))
            // visit website
            7 -> CustomTabsIntent.Builder().setShowTitle(true)
                    .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setStartAnimations(this, R.anim.slide_in_right,
                            R.anim.slide_out_left)
                    .setExitAnimations(this, android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right)
                    .build()
                    .launchUrl(this, Uri.parse(Constants.WEBSITE))
            // read more from Reddit
            8 -> CustomTabsIntent.Builder().setShowTitle(true)
                    .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setStartAnimations(this, R.anim.slide_in_right,
                            R.anim.slide_out_left)
                    .setExitAnimations(this, android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right).build()
                    .launchUrl(this, Uri.parse(Constants.REDDIT))
            // donate
            11 -> bp.purchase(this, Constants.PRODUCT_ID_1)
            12 -> bp.purchase(this, Constants.PRODUCT_ID_2)
            13 -> bp.purchase(this, Constants.PRODUCT_ID_3)
        }
        return true
    }
}