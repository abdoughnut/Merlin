package com.abdodaoud.merlin.domain.datasource

import com.abdodaoud.merlin.domain.model.Fact
import com.abdodaoud.merlin.domain.model.FactList

interface FactDataSource {
    fun request(date: Long, currentPage: Int, lastDate: Long): FactList?

    fun requestDayFact(id: Long): Fact?
}