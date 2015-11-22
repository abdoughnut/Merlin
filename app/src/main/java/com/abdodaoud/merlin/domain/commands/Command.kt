package com.abdodaoud.merlin.domain.commands

interface Command<T> {
    fun execute(): T
}