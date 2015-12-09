package com.abdodaoud.merlin.data.server

import com.abdodaoud.merlin.util.Constants
import com.google.gson.Gson
import java.net.URL

class FactRequest(val gson: Gson = Gson()) {

    companion object {
        private val COMPLETE_URL = Constants.FACTS_URL
    }

    fun execute(currentPage: Int): FactResult {
        val factJsonStr = URL(COMPLETE_URL).readText()
        return gson.fromJson(factJsonStr, FactResult::class.java)
    }
}