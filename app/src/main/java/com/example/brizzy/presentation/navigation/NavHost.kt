package com.example.brizzy.presentation.navigation


import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.brizzy.data.weather.WeatherRepositoryImp
import com.example.brizzy.data.weather.datasource.local.dataStore.SettingsPreferencesManager
import com.example.brizzy.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import com.example.brizzy.presentation.alerts.view.AlertsScreen
import com.example.brizzy.presentation.favorite.view.FavoritesScreen
import com.example.brizzy.presentation.home.view.HomeScreen
import com.example.brizzy.presentation.home.viewModel.HomeViewModel
import com.example.brizzy.presentation.home.viewModel.HomeViewModelFactory
import com.example.brizzy.presentation.map.view.MapScreen
import com.example.brizzy.presentation.settings.view.SettingsScreen
import com.example.brizzy.presentation.settings.viewModel.SettingsViewModel
import com.example.brizzy.presentation.settings.viewModel.SettingsViewModelFactory
import com.example.brizzy.presentation.splash.view.SplashScreen


@Composable
fun SetupNavHost() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route?.contains("Splash") == false

    val context = LocalContext.current
    val prefsManager = remember { SettingsPreferencesManager(context) }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                BrizzyBottomNavigationBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ScreenRouts.Splash,
        ) {
            // Splash Screen
            composable<ScreenRouts.Splash> {
                SplashScreen(navController = navController)
            }

            // Home Screen
            composable<ScreenRouts.Home> {
                val remoteDataSource = WeatherRemoteDataSourceImpl()
                val repository = WeatherRepositoryImp(remoteDataSource)
                val factory = HomeViewModelFactory(repository, prefsManager)
                val homeViewModel: HomeViewModel = viewModel(factory = factory)

                HomeScreen(viewModel = homeViewModel)
            }

            // Favorite Screen
            composable<ScreenRouts.Favorite> {
                FavoritesScreen()
            }

            // Alerts Screen
            composable<ScreenRouts.Alerts> {
                AlertsScreen()
            }

            // Settings Screen
            composable<ScreenRouts.Settings> {
                val factory = remember { SettingsViewModelFactory(prefsManager) }
                val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

                SettingsScreen(viewModel = settingsViewModel,navController=navController)
            }

            // Map Screen
            composable<ScreenRouts.MapSelection> {
                val factory = remember { SettingsViewModelFactory(prefsManager) }
                val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

                MapScreen(
                    onLocationSelected = { lat, lon ->
                        settingsViewModel.updateMapLocation(lat, lon)
                        settingsViewModel.updateLocationMethod("Map")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}