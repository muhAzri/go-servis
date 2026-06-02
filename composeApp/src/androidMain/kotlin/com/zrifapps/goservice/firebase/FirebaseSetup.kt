package com.zrifapps.goservice.firebase

import android.app.Application
import android.content.pm.ApplicationInfo
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.perf.performance

/**
 * Firebase is auto-initialized by its startup [androidx.startup]/ContentProvider before
 * [Application.onCreate], so this only tunes data collection: we keep debug-session crashes,
 * events, and traces out of the production dashboards and only collect from release builds.
 */
object FirebaseSetup {
    fun init(app: Application) {
        val isDebuggable = (app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val collect = !isDebuggable

        Firebase.crashlytics.setCrashlyticsCollectionEnabled(collect)
        Firebase.analytics.setAnalyticsCollectionEnabled(collect)
        Firebase.performance.isPerformanceCollectionEnabled = collect
    }
}
