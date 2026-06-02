package com.zrifapps.goservice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zrifapps.goservice.core.notification.OdometerNotifier
import com.zrifapps.goservice.core.notification.ReminderNotifier
import com.zrifapps.goservice.navigation.AppNavGraph
import com.zrifapps.goservice.navigation.OdometerDeepLinks
import com.zrifapps.goservice.navigation.ReminderDeepLinks
import com.zrifapps.goservice.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppNavGraph()
            }
        }
        handleNotificationIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        intent.getStringExtra(ReminderNotifier.EXTRA_REMINDER_ID)?.let { reminderId ->
            intent.removeExtra(ReminderNotifier.EXTRA_REMINDER_ID)
            ReminderDeepLinks.open(reminderId)
        }
        intent.getStringExtra(OdometerNotifier.EXTRA_VEHICLE_ID)?.let { vehicleId ->
            intent.removeExtra(OdometerNotifier.EXTRA_VEHICLE_ID)
            OdometerDeepLinks.open(vehicleId)
        }
    }
}
