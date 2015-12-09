package com.abdodaoud.merlin.domain.model

data class FactList(val id: Long, val dailyFact: List<Fact>) {
    operator fun get(position: Int) = dailyFact[position]
    fun size() = dailyFact.size
}

data class Fact(val id: Long, val created: Long, val title: String, val url: String)