package com.abdodaoud.merlin.domain.commands

import com.abdodaoud.merlin.domain.datasource.FactProvider
import com.abdodaoud.merlin.domain.model.Fact

class RequestDayFactCommand(
        val id: Long,
        val factProvider: FactProvider = FactProvider()) :
        Command<Fact> {

    override fun execute(currentPage: Int, lastDate: Long) = factProvider.requestFact(id)
}
