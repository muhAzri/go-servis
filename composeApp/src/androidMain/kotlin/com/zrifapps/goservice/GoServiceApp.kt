package com.zrifapps.goservice

import android.app.Application
import com.zrifapps.goservice.ads.AdsManager

class GoServiceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AdsManager.initialize(this)
    }
}
