package com.example.brizzy.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRouts {
    @Serializable
    data object Splash : ScreenRouts()

    @Serializable
    data object Home : ScreenRouts()

    @Serializable
    data object Alerts : ScreenRouts()

    @Serializable
    data object Favorite : ScreenRouts()

    @Serializable
    data object Settings : ScreenRouts()

    @Serializable
    data object MapSelection : ScreenRouts()
}