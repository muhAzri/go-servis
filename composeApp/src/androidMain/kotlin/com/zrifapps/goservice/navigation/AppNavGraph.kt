package com.zrifapps.goservice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zrifapps.goservice.ui.home.HomeScreen
import com.zrifapps.goservice.ui.splash.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
    ) {
        composable<Screen.Splash> {
            SplashScreen {
                navController.navigate(Screen.Home) {
                    popUpTo<Screen.Splash> { inclusive = true }
                }
            }
        }

        composable<Screen.Home> {
            HomeScreen()
        }
    }
}
