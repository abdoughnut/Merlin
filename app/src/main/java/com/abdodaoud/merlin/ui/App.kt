package com.abdodaoud.merlin.ui

import android.app.Application
import com.abdodaoud.merlin.extensions.DelegatesExtensions

class App : Application() {

    companion object {
        var instance: App by DelegatesExtensions.notNullSingleValue()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}