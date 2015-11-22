package com.abdodaoud.merlin.data.server

data class FactResult(val data: Data)
data class Data(val modhash: String, val children: List<Children>)
data class Children(val data: Fact)
data class Fact(val created: Long, val title: String, val url: String)