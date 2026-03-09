package com.example.brizzy.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem<T : Any>(val route: T, val title: String, val icon: ImageVector) {
    data object Home : BottomNavItem<ScreenRouts.Home>(ScreenRouts.Home, "Home", Icons.Default.Home)
    data object Favorite : BottomNavItem<ScreenRouts.Favorite>(ScreenRouts.Favorite, "Favorite", Icons.Default.Favorite)
    data object Alerts : BottomNavItem<ScreenRouts.Alerts>(ScreenRouts.Alerts, "Alerts", Icons.Default.Notifications)
    data object Settings : BottomNavItem<ScreenRouts.Settings>(ScreenRouts.Settings, "Settings", Icons.Default.Settings)
}