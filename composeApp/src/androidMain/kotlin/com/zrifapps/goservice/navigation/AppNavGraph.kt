package com.zrifapps.goservice.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.zrifapps.goservice.ads.AdsManager
import com.zrifapps.goservice.ui.legal.AboutScreen
import com.zrifapps.goservice.ui.legal.HelpScreen
import com.zrifapps.goservice.ui.legal.PrivacyScreen
import com.zrifapps.goservice.ui.legal.TermsScreen
import com.zrifapps.goservice.ui.main.MainTabsScreen
import com.zrifapps.goservice.ui.test.TestScreen
import com.zrifapps.goservice.ui.vehicle.AddVehicleScreen
import com.zrifapps.goservice.ui.onboarding.NotifPermScreen
import com.zrifapps.goservice.ui.onboarding.OnboardingAddVehicleScreen
import com.zrifapps.goservice.ui.onboarding.OnboardingScreen
import com.zrifapps.goservice.ui.onboarding.PickVehicleTypeScreen
import com.zrifapps.goservice.ui.splash.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("servisgo_prefs", Context.MODE_PRIVATE) }
    val onboardingDone = remember { prefs.getBoolean("onboarding_done", false) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        AdsManager.setAppOpenAllowed(currentRoute == Screen.Main::class.qualifiedName)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
    ) {
        composable<Screen.Splash> {
            SplashScreen {
                if (onboardingDone) {
                    navController.navigate(Screen.Main) {
                        popUpTo<Screen.Splash> { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Onboarding) {
                        popUpTo<Screen.Splash> { inclusive = true }
                    }
                }
            }
        }

        composable<Screen.Onboarding> {
            OnboardingScreen(
                onSkip = {
                    prefs.edit().putBoolean("onboarding_done", true).apply()
                    navController.navigate(Screen.Main) {
                        popUpTo<Screen.Onboarding> { inclusive = true }
                    }
                },
                onFinish = {
                    navController.navigate(Screen.PickVehicleType)
                },
            )
        }

        composable<Screen.PickVehicleType> {
            PickVehicleTypeScreen(
                onBack = { navController.popBackStack() },
                onPickType = {
                    navController.navigate(Screen.AddVehicle(it))
                },
            )
        }

        composable<Screen.AddVehicle> { backStackEntry ->
            val screen = backStackEntry.toRoute<Screen.AddVehicle>()
            OnboardingAddVehicleScreen(
                vehicleType = screen.type,
                onBack = { navController.popBackStack() },
                onComplete = { navController.navigate(Screen.NotifPermission) },
            )
        }

        composable<Screen.NotifPermission> {
            NotifPermScreen(
                onBack = { navController.popBackStack() },
                onComplete = {
                    prefs.edit().putBoolean("onboarding_done", true).apply()
                    navController.navigate(Screen.Main) {
                        popUpTo<Screen.Onboarding> { inclusive = true }
                    }
                },
            )
        }

        composable<Screen.Main> {
            MainTabsScreen(
                onAddService = { navController.navigate(Screen.AddVehicleForm) },
                onOpenTestScreen = { navController.navigate(Screen.Test) },
                onOpenPrivacy = { navController.navigate(Screen.Privacy) },
                onOpenTerms = { navController.navigate(Screen.Terms) },
                onOpenAbout = { navController.navigate(Screen.About) },
                onOpenHelp = { navController.navigate(Screen.Help) },
            )
        }

        composable<Screen.Test> {
            TestScreen(
                onBack = { navController.popBackStack() },
                onAddVehicle = { navController.navigate(Screen.AddVehicleForm) },
            )
        }

        composable<Screen.AddVehicleForm> {
            AddVehicleScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable<Screen.Privacy> {
            PrivacyScreen(onBack = { navController.popBackStack() })
        }

        composable<Screen.Terms> {
            TermsScreen(onBack = { navController.popBackStack() })
        }

        composable<Screen.About> {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable<Screen.Help> {
            HelpScreen(onBack = { navController.popBackStack() })
        }
    }
}
