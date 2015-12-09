package com.abdodaoud.merlin.data.server

import android.text.Html
import com.abdodaoud.merlin.domain.model.FactList
import com.abdodaoud.merlin.extensions.future
import com.abdodaoud.merlin.extensions.zeroedTime
import com.abdodaoud.merlin.domain.model.Fact as ModelForecast

class ServerDataMapper {
    fun convertToDomain(factList: FactResult): FactList = with(factList) {
        FactList(-1, convertForecastListToDomain(data.children))
    }

    private fun convertForecastListToDomain(children: List<Children>): List<ModelForecast> {
        return children.map { convertForecastItemToDomain(it) }
    }

    private fun convertForecastItemToDomain(fact: Children): ModelForecast = with(fact) {
        ModelForecast(-1, (data.created*1000).zeroedTime().future(7), cleanUpFact(data.title), data.url)
    }

    private fun cleanUpFact(title: String): String {
        val cleanedUpFact = Html.fromHtml(title).trim().substring(3).trim()
        if (cleanedUpFact.substring(0, 4).equals("that", true)) {
            return cleanedUpFact.substring(4).trim().capitalize()
        }
        return cleanedUpFact.capitalize()
    }
}