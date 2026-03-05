package com.example.brizzy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.brizzy.presentation.splash.view.SplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brizzy.data.weather.WeatherRepositoryImp
import com.example.brizzy.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import com.example.brizzy.presentation.home.view.HomeScreen
import com.example.brizzy.presentation.home.viewModel.HomeViewModel
import com.example.brizzy.presentation.home.viewModel.HomeViewModelFactory

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
            val remoteDataSource = WeatherRemoteDataSourceImpl()
            val repository = WeatherRepositoryImp(remoteDataSource)
            val factory = HomeViewModelFactory(repository)
            val homeViewModel: HomeViewModel = viewModel(factory = factory)

            HomeScreen(viewModel = homeViewModel)
        }
    }
}