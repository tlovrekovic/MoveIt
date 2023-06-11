package com.lovrekovic.moveit

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

//annotacija, dagger handla injection
@HiltAndroidApp
class BaseApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        //logging
        Timber.plant(Timber.DebugTree())
    }

}