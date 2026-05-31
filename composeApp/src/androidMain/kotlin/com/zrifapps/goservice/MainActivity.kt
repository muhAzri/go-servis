package com.zrifapps.goservice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zrifapps.goservice.core.notification.ReminderNotifier
import com.zrifapps.goservice.navigation.AppNavGraph
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
        val reminderId = intent?.getStringExtra(ReminderNotifier.EXTRA_REMINDER_ID) ?: return
        intent.removeExtra(ReminderNotifier.EXTRA_REMINDER_ID)
        ReminderDeepLinks.open(reminderId)
    }
}
