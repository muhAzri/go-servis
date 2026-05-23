package com.zrifapps.goservice

import android.app.Application
import com.zrifapps.goservice.ads.AdsManager
import com.zrifapps.goservice.core.di.runStartupTasks
import com.zrifapps.goservice.core.di.startAppKoin
import org.koin.android.ext.koin.androidContext

class GoServiceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startAppKoin {
            androidContext(this@GoServiceApp)
        }
        runStartupTasks()
        AdsManager.initialize(this)
    }
}
