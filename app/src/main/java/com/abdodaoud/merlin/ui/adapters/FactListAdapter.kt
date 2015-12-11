package com.abdodaoud.merlin.ui.adapters

import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.abdodaoud.merlin.R
import com.abdodaoud.merlin.domain.model.Fact
import com.abdodaoud.merlin.domain.model.FactList
import com.abdodaoud.merlin.extensions.ctx
import com.abdodaoud.merlin.extensions.parseMessage
import com.abdodaoud.merlin.extensions.toDateString
import kotlinx.android.synthetic.item_fact.view.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick

class FactListAdapter(val facts: FactList) :
        RecyclerView.Adapter<FactListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        val view = parent.ctx.layoutInflater.inflate(R.layout.item_fact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(facts[position])
    }

    override fun getItemCount() = facts.size()

    fun getItemAtPosition(position: Int) = facts.get(position)

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val mContext = view.context

        fun bindForecast(fact: Fact) {
            with(fact) {
                itemView.title.text = fact.title
                itemView.date.text = fact.created.toDateString()
                itemView.onClick { itemClick(fact) }
                itemView.onLongClick { itemLongClick(fact) }
            }
        }

        private fun itemLongClick(fact: Fact): Boolean {
            val builderSingle = AlertDialog.Builder(mContext)
            val arrayAdapter = ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item)
            arrayAdapter.add(mContext.getString(R.string.action_view_source))
            arrayAdapter.add(mContext.getString(R.string.action_share))
            builderSingle.setAdapter(arrayAdapter) { dialog, which ->
                when(which) {
                    0 -> CustomTabsIntent.Builder().setShowTitle(true)
                            .setToolbarColor(mContext.getColor(R.color.colorPrimary))
                            .setStartAnimations(mContext, R.anim.slide_in_right,
                                    R.anim.slide_out_left)
                            .setExitAnimations(mContext, android.R.anim.slide_in_left,
                                    android.R.anim.slide_out_right).build()
                            .launchUrl(mContext as AppCompatActivity, Uri.parse(fact.url))
                    1 -> mContext.startActivity(Intent(Intent(Intent.ACTION_SEND)
                                .putExtra(Intent.EXTRA_TEXT, fact.title.parseMessage())
                                .setType("text/plain")))
                }
            }
            builderSingle.show()
            return true
        }

        private fun itemClick(fact: Fact) {
            // TODO: Add favourite feature
        }
    }
}