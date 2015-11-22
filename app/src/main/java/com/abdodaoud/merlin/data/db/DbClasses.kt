package com.abdodaoud.merlin.data.db

import java.util.*
import kotlin.properties.getValue
import kotlin.properties.setValue

class MainFacts(val map: MutableMap<String, Any?>, val dailyFact: List<DayFact>) {
    var _id: Long by map

    constructor(id: Long, dailyFact: List<DayFact>) : this(HashMap(), dailyFact) {
        this._id = id
    }
}

class DayFact(var map: MutableMap<String, Any?>) {
    var _id: Long by map
    var factsId: Long by map
    var date: Long by map
    var title: String by map
    var url: String by map

    constructor(factsId: Long, date: Long, title: String, url: String)
    : this(HashMap()) {
        this.factsId = factsId
        this.date = date
        this.title = title
        this.url = url
    }
}