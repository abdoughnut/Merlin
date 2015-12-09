package com.abdodaoud.merlin.domain.datasource

import com.abdodaoud.merlin.data.db.FactDb
import com.abdodaoud.merlin.data.server.FactServer
import com.abdodaoud.merlin.domain.model.Fact
import com.abdodaoud.merlin.domain.model.FactList
import com.abdodaoud.merlin.extensions.firstResult
import com.abdodaoud.merlin.extensions.maxDate
import com.abdodaoud.merlin.extensions.zeroedTime

class FactProvider(val sources: List<FactDataSource> = FactProvider.SOURCES) {

    companion object {
        val DAY_IN_MILLIS = 1000 * 60 * 60 * 24
        val SOURCES = listOf(FactDb(), FactServer())
    }

    fun request(currentPage: Int, lastDate: Long): FactList = requestToSources {
        val res = it.request(todayTimeSpan(), currentPage, lastDate)
        if (res != null && hasTodayFact(res, currentPage)) res else null
    }

    private fun hasTodayFact(res: FactList, currentPage: Int): Boolean {
        if (currentPage == -1) currentPage == 1
        val created = System.currentTimeMillis().zeroedTime()
        for (fact in res.dailyFact) {
            if (fact.created == created.maxDate(currentPage)) return true
        }
        return false
    }

    fun requestFact(id: Long): Fact = requestToSources { it.requestDayFact(id) }

    private fun todayTimeSpan() = System.currentTimeMillis().zeroedTime() /
            DAY_IN_MILLIS * DAY_IN_MILLIS

    private fun <T : Any> requestToSources(f: (FactDataSource) -> T?): T =
            sources.firstResult { f(it) }

}