package com.example.brizzy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.brizzy.presentation.splash.view.SplashScreen

@Composable
fun SetupNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ScreenRouts.Splash
    ) {
        //splash
        composable<ScreenRouts.Splash> {
            SplashScreen(navController = navController)
        }

        // Home
        composable<ScreenRouts.Home> {
            Text(text = "Welcome to Home Screen!")
        }
    }
}