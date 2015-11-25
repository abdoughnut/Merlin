package com.abdodaoud.merlin.data.server

import com.google.gson.Gson
import java.net.URL

class FactRequest(val gson: Gson = Gson()) {

    companion object {
        private val USER = "merlinoftheday"
        private val URL = "https://api.reddit.com"
        private val COMPLETE_URL = "$URL/user/$USER/upvoted"
    }

    fun execute(): FactResult {
        val factJsonStr = URL(COMPLETE_URL).readText()
        return gson.fromJson(factJsonStr, FactResult::class.java)
    }
}