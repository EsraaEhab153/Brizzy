package com.example.brizzy.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRouts {
    @Serializable
    data object Splash : ScreenRouts()

    @Serializable
    data object Home : ScreenRouts()
}