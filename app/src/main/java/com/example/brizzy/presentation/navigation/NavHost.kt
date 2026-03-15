package com.example.brizzy.presentation.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.brizzy.data.db.WeatherDatabase
import com.example.brizzy.data.network.RetrofitClient
import com.example.brizzy.data.weather.WeatherRepositoryImp
import com.example.brizzy.data.weather.datasource.local.dataStore.SettingsPreferencesManager
import com.example.brizzy.data.weather.datasource.local.room.WeatherLocalDataSource
import com.example.brizzy.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import com.example.brizzy.data.weather.model.FavoriteLocationEntity
import com.example.brizzy.presentation.alerts.view.AlertsScreen
import com.example.brizzy.presentation.alerts.viewModel.AlertsViewModel
import com.example.brizzy.presentation.alerts.viewModel.AlertsViewModelFactory
import com.example.brizzy.presentation.favorite.view.FavoritesScreen
import com.example.brizzy.presentation.favorite.viewModel.FavoritesViewModel
import com.example.brizzy.presentation.favorite.viewModel.FavoritesViewModelFactory
import com.example.brizzy.presentation.home.view.HomeScreen
import com.example.brizzy.presentation.home.viewModel.HomeViewModel
import com.example.brizzy.presentation.home.viewModel.HomeViewModelFactory
import com.example.brizzy.presentation.map.view.MapScreen
import com.example.brizzy.presentation.map.viewModel.MapViewModel
import com.example.brizzy.presentation.map.viewModel.MapViewModelFactory
import com.example.brizzy.presentation.settings.view.SettingsScreen
import com.example.brizzy.presentation.settings.viewModel.SettingsViewModel
import com.example.brizzy.presentation.settings.viewModel.SettingsViewModelFactory
import com.example.brizzy.presentation.splash.view.SplashScreen
import kotlinx.coroutines.launch

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
                val database = WeatherDatabase.getDatabase(context)
                val localDataSource = WeatherLocalDataSource(database.favoriteLocationDao(),database.alertDao())
                val remoteDataSource = WeatherRemoteDataSourceImpl()

                val repository = WeatherRepositoryImp(
                    remoteDataSource = remoteDataSource,
                    localDataSource = localDataSource
                )
                val factory = HomeViewModelFactory(repository, prefsManager)
                val homeViewModel: HomeViewModel = viewModel(factory = factory)

                HomeScreen(viewModel = homeViewModel)
            }

            // Favorites Screen
            composable<ScreenRouts.Favorite> {
                val database = WeatherDatabase.getDatabase(context)
                val repository = remember {
                    WeatherRepositoryImp(
                        remoteDataSource = WeatherRemoteDataSourceImpl(),
                        localDataSource = WeatherLocalDataSource(database.favoriteLocationDao(), database.alertDao())
                    )
                }

                val favoritesFactory = remember { FavoritesViewModelFactory(repository) }
                val favoritesViewModel: FavoritesViewModel = viewModel(factory = favoritesFactory)

                FavoritesScreen(viewModel = favoritesViewModel,
                    onAddLocationClick = {
                        navController.navigate(ScreenRouts.MapAddFavorite)
                    },
                    onLocationClick = { lat, lon ->
                        navController.navigate(ScreenRouts.FavoriteDetails(lat = lat, lon = lon))
                    }
                )
            }

            // Alerts Screen
            composable<ScreenRouts.Alerts> {
                val database = WeatherDatabase.getDatabase(context)
                val repository = remember {
                    WeatherRepositoryImp(
                        remoteDataSource = WeatherRemoteDataSourceImpl(),
                        localDataSource = WeatherLocalDataSource(database.favoriteLocationDao(), database.alertDao())
                    )
                }

                val alertsFactory = remember { AlertsViewModelFactory(repository) }
                val alertsViewModel: AlertsViewModel = viewModel(factory = alertsFactory)
                val mapViewModel: MapViewModel = viewModel(factory = MapViewModelFactory(repository))

                AlertsScreen(viewModel = alertsViewModel,mapViewModel = mapViewModel)
            }

            // Settings Screen
            composable<ScreenRouts.Settings> {
                val factory = remember { SettingsViewModelFactory(prefsManager) }
                val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

                SettingsScreen(viewModel = settingsViewModel, navController = navController)
            }

            // Map Screen
            composable<ScreenRouts.MapSelection> {
                val settingsFactory = remember { SettingsViewModelFactory(prefsManager) }
                val settingsViewModel: SettingsViewModel = viewModel(factory = settingsFactory)

                val repository = remember {
                    val database = WeatherDatabase.getDatabase(context)
                    val favoriteDao = database.favoriteLocationDao()
                    WeatherRepositoryImp(
                        remoteDataSource = WeatherRemoteDataSourceImpl(),
                        localDataSource = WeatherLocalDataSource(favoriteDao,database.alertDao())
                    )
                }
                val mapFactory = remember { MapViewModelFactory(repository) }
                val mapViewModel: MapViewModel = viewModel(factory = mapFactory)

                MapScreen(
                    viewModel = mapViewModel,
                    onLocationSelected = { cityName,lat, lon ->
                        settingsViewModel.updateMapLocation(lat, lon)
                        settingsViewModel.updateLocationMethod("Map")
                        navController.popBackStack()
                    }
                )
            }

            composable<ScreenRouts.MapAddFavorite> {
                val scope = rememberCoroutineScope()
                val database = WeatherDatabase.getDatabase(context)
                val repository = remember {
                    WeatherRepositoryImp(
                        remoteDataSource = WeatherRemoteDataSourceImpl(),
                        localDataSource = WeatherLocalDataSource(database.favoriteLocationDao(),database.alertDao())
                    )
                }
                val mapFactory = remember { MapViewModelFactory(repository) }
                val mapViewModel: MapViewModel = viewModel(factory = mapFactory)

                MapScreen(
                    viewModel = mapViewModel,
                    onLocationSelected = { cityName, lat, lon ->
                        val newFavorite = FavoriteLocationEntity(
                            cityName = cityName,
                            latitude = lat,
                            longitude = lon
                        )

                        scope.launch {
                            repository.insertFavoriteLocation(newFavorite)
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable<ScreenRouts.FavoriteDetails> { backStackEntry ->
                val details = backStackEntry.toRoute<ScreenRouts.FavoriteDetails>()

                val remoteDataSource = WeatherRemoteDataSourceImpl()
                val localDataSource = WeatherLocalDataSource(WeatherDatabase.getDatabase(context).favoriteLocationDao(),WeatherDatabase.getDatabase(context).alertDao())
                val repository = WeatherRepositoryImp(remoteDataSource, localDataSource)

                val factory = HomeViewModelFactory(
                    repository = repository,
                    prefsManager = prefsManager,
                    isFavoriteMode = true
                )
                val detailsViewModel: HomeViewModel = viewModel(factory = factory)

                 LaunchedEffect(details) {
                     detailsViewModel.getWeatherData(
                         lat = details.lat,
                         lon = details.lon,
                     )
                }

                HomeScreen(viewModel = detailsViewModel)
            }
        }
    }
}