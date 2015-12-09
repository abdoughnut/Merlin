package com.abdodaoud.merlin.extensions

import android.text.format.DateUtils
import java.text.DateFormat
import java.util.*

fun Long.toDateString(dateFormat: Int = DateFormat.MEDIUM): String {
    val df = DateFormat.getDateInstance(dateFormat, Locale.getDefault())
    return df.format(this)
}

fun Long.maxDate(currentPage: Int = 1): Long {
    if (currentPage == 1) return this
    return this.minDate(currentPage-1).past()
}

fun Long.minDate(currentPage: Int = 1): Long {
    return this.future().past(currentPage * 10)
}

fun Long.zeroedTime(): Long {
    return this - (this % DateUtils.DAY_IN_MILLIS)
}

fun Long.past(days: Int = 1): Long {
    return this - (days * DateUtils.DAY_IN_MILLIS)
}

fun Long.future(days: Int = 1): Long {
    return this + (days * DateUtils.DAY_IN_MILLIS)
}

// TODO: clean up the message to be shared
fun String.parseMessage(): String {
    return this
}