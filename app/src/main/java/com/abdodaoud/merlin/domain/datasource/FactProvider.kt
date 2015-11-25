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

    fun request(): FactList = requestToSources {
        val res = it.request(todayTimeSpan())
        if (res != null && hasTodayFact(res)) res else null
    }

    private fun hasTodayFact(res: FactList): Boolean {
        val created = System.currentTimeMillis() - (System.currentTimeMillis() % DateUtils.DAY_IN_MILLIS)
        for (fact in res.dailyFact) {
            if (fact.created == created) return true
        }
        return false
    }

    fun requestFact(id: Long): Fact = requestToSources { it.requestDayFact(id) }

    private fun todayTimeSpan() = System.currentTimeMillis() -
            (System.currentTimeMillis() % DateUtils.DAY_IN_MILLIS) / DAY_IN_MILLIS * DAY_IN_MILLIS

    private fun <T : Any> requestToSources(f: (FactDataSource) -> T?): T = sources.firstResult { f(it) }

}