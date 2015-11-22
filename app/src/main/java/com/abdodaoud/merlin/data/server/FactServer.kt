package com.abdodaoud.merlin.data.server

import com.abdodaoud.merlin.data.db.FactDb
import com.abdodaoud.merlin.domain.datasource.FactDataSource
import com.abdodaoud.merlin.domain.model.Fact
import com.abdodaoud.merlin.domain.model.FactList

class FactServer(val dataMapper: ServerDataMapper = ServerDataMapper(),
                     val factDb: FactDb = FactDb()) : FactDataSource {

    override fun request(date: Long): FactList? {
        val result = FactRequest().execute()
        val converted = dataMapper.convertToDomain(result)
        factDb.saveFact(converted)
        return factDb.request(date)
    }

    override fun requestDayFact(id: Long): Fact? = throw UnsupportedOperationException()
}
