package com.abdodaoud.merlin.data.db

import com.abdodaoud.merlin.domain.model.Fact
import com.abdodaoud.merlin.domain.model.FactList

class DbDataMapper {

    fun convertFromDomain(factList: FactList) = with(factList) {
        val daily = dailyFact map { convertDayFromDomain(id, it) }
        MainFacts(id, daily)
    }

    fun convertDayFromDomain(factId: Long, fact: Fact) = with(fact) {
        DayFact(factId, fact.created, fact.title, fact.url)
    }

    fun convertToDomain(factList: MainFacts) = with(factList) {
        val daily = dailyFact map { convertDayToDomain(it) }
        FactList(_id, daily)
    }

    fun convertDayToDomain(dayForecast: DayFact) = with(dayForecast) {
        Fact(_id, date, title, url)
    }
}