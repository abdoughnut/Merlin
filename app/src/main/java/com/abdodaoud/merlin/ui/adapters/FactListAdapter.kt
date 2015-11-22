package com.abdodaoud.merlin.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.abdodaoud.merlin.R
import com.abdodaoud.merlin.domain.model.Fact
import com.abdodaoud.merlin.domain.model.FactList
import com.abdodaoud.merlin.extensions.ctx
import com.abdodaoud.merlin.extensions.toDateString
import kotlinx.android.synthetic.item_fact.view.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick

class FactListAdapter(val facts: FactList, val itemClick: (Fact) -> Unit) :
        RecyclerView.Adapter<FactListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        val view = parent.ctx.layoutInflater.inflate(R.layout.item_fact, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(facts[position])
    }

    override fun getItemCount() = facts.size()

    class ViewHolder(view: View, val itemClick: (Fact) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindForecast(fact: Fact) {
            with(fact) {
                itemView.title.text = fact.title
                itemView.date.text = fact.created.toDateString()
                itemView.onClick { itemClick(fact) }
            }
        }
    }
}