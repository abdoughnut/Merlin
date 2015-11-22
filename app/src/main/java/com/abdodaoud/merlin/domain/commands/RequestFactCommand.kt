package com.abdodaoud.merlin.domain.commands

import com.abdodaoud.merlin.domain.datasource.FactProvider
import com.abdodaoud.merlin.domain.model.FactList

class RequestFactCommand(
        val factProvider: FactProvider = FactProvider()) :
        Command<FactList> {

    companion object {
        val DAYS = 3
    }

    override fun execute() = factProvider.request(DAYS)
}