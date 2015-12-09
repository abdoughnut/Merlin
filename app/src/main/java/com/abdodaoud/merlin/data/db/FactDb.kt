package com.abdodaoud.merlin.data.db

import com.abdodaoud.merlin.domain.datasource.FactDataSource
import com.abdodaoud.merlin.domain.model.Fact
import com.abdodaoud.merlin.domain.model.FactList
import com.abdodaoud.merlin.extensions.*
import org.jetbrains.anko.db.SqlOrderDirection
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import java.util.*

class FactDb(val factDbHelper: FactDbHelper = FactDbHelper.instance,
                 val dataMapper: DbDataMapper = DbDataMapper()) : FactDataSource {

    override fun requestDayFact(id: Long): Fact? = factDbHelper.use {
        val fact = select(DayFactTable.NAME).byId(id).
                parseOpt { DayFact(HashMap(it)) }

        fact?.let { dataMapper.convertDayToDomain(it) }
    }

    override fun request(date: Long, currentPage: Int, lastDate: Long) = factDbHelper.use {
        var maxDate = date
        var minDate = date

        if (currentPage == -1) {
            minDate = lastDate.future()
        } else {
            maxDate = date.maxDate(currentPage)
            minDate = date.minDate(currentPage)
        }

        val dailyFact = select(DayFactTable.NAME)
                .where("${DayFactTable.DATE} <= " + maxDate.toString() + " AND " +
                        "${DayFactTable.DATE} >= " + minDate.toString())
                .orderBy(DayFactTable.DATE, SqlOrderDirection.DESC)
                .parseList { DayFact(HashMap(it)) }

        val mainFacts = select(MainFactsTable.NAME)
                .whereSimple("${MainFactsTable.ID} = ?", "-1")
                .parseOpt { MainFacts(HashMap(it), dailyFact) }

        mainFacts?.let { dataMapper.convertToDomain(it) }
    }

    fun saveFact(facts: FactList) = factDbHelper.use {

        clear(MainFactsTable.NAME)
        clear(DayFactTable.NAME)

        with(dataMapper.convertFromDomain(facts)) {
            insert(MainFactsTable.NAME, *map.toVarargArray())
            dailyFact.forEach { insert(DayFactTable.NAME, *it.map.toVarargArray()) }
        }
    }
}