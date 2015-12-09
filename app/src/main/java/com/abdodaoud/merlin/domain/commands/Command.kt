package com.abdodaoud.merlin.domain.commands

interface Command<T> {
    fun execute(currentPage: Int, lastDate: Long): T
}