package com.abdodaoud.merlin.domain.datasource

import android.text.format.DateUtils
import com.abdodaoud.merlin.data.db.FactDb
import com.abdodaoud.merlin.data.server.FactServer
import com.abdodaoud.merlin.domain.model.Fact
import com.abdodaoud.merlin.domain.model.FactList
import com.abdodaoud.merlin.extensions.firstResult

class FactProvider(val sources: List<FactDataSource> = FactProvider.SOURCES) {

    companion object {
        val DAY_IN_MILLIS = 1000 * 60 * 60 * 24
        val SOURCES = listOf(FactDb(), FactServer())
    }

    fun request(days: Int): FactList = requestToSources {
        val res = it.request(tomorrowTimeSpan())
        if (res != null && res.size() >= days) res else null
    }

    fun requestFact(id: Long): Fact = requestToSources { it.requestDayFact(id) }

    private fun tomorrowTimeSpan() = System.currentTimeMillis() + DateUtils.DAY_IN_MILLIS +
            (DateUtils.DAY_IN_MILLIS - System.currentTimeMillis() % DateUtils.DAY_IN_MILLIS) /
                    DAY_IN_MILLIS * DAY_IN_MILLIS

    private fun <T : Any> requestToSources(f: (FactDataSource) -> T?): T = sources.firstResult { f(it) }

}