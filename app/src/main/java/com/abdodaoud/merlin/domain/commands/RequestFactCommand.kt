package com.abdodaoud.merlin.domain.commands

import com.abdodaoud.merlin.domain.datasource.FactProvider
import com.abdodaoud.merlin.domain.model.FactList

class RequestFactCommand(
        val factProvider: FactProvider = FactProvider()) :
        Command<FactList> {

    override fun execute(currentPage: Int, lastDate: Long) =
            factProvider.request(currentPage, lastDate)
}