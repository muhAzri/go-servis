package com.zrifapps.goservice

import android.app.Application
import com.zrifapps.goservice.ads.AdsManager
import com.zrifapps.goservice.core.di.runStartupTasks
import com.zrifapps.goservice.core.di.startAppKoin
import com.zrifapps.goservice.firebase.FirebaseSetup
import org.koin.android.ext.koin.androidContext

class GoServiceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseSetup.init(this)
        startAppKoin {
            androidContext(this@GoServiceApp)
        }
        runStartupTasks()
        AdsManager.initialize(this)
    }
}
