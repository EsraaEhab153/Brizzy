package com.example.brizzy.presentation.settings.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brizzy.R
import com.example.brizzy.presentation.navigation.ScreenRouts
import com.example.brizzy.presentation.settings.viewModel.SettingsViewModel
import com.example.brizzy.presentation.theme.WeatherThemeState

@Composable
fun SettingsScreen(viewModel: SettingsViewModel,navController: NavController) {

    val selectedTemp by viewModel.tempUnit.collectAsState()
    val selectedWind by viewModel.windUnit.collectAsState()
    val selectedLocation by viewModel.locationMethod.collectAsState()
    val selectedLanguage by viewModel.language.collectAsState()

    val bgGradient = Brush.verticalGradient(
        colors = WeatherThemeState.currentColors
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {

            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Customize your Brizzy experience",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            SectionTitle("UNITS")
            SettingsCard {
                // Temperature
                SettingsItem(
                    iconRes = R.drawable.thermostat,
                    title = "Temperature",
                    subtitle = "Currently: $selectedTemp",
                    options = listOf("Celsius", "Fahrenheit", "Kelvin"),
                    selectedOption = selectedTemp,
                    onOptionSelected = { viewModel.updateTempUnit(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Wind Speed
                SettingsItem(
                    iconRes = R.drawable.barometer,
                    title = "Wind Speed",
                    subtitle = "Currently: $selectedWind",
                    options = listOf("m/s", "mph"),
                    selectedOption = selectedWind,
                    onOptionSelected = { viewModel.updateWindUnit(it) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            //Location
            SectionTitle("LOCATION")
            SettingsCard {
                SettingsItem(
                    iconRes = R.drawable.gps,
                    title = "Location Method",
                    subtitle = "Currently: $selectedLocation",
                    options = listOf("GPS", "Map"),
                    selectedOption = selectedLocation,
                    onOptionSelected = { option ->
                        if (option == "Map") {
                            navController.navigate(ScreenRouts.MapSelection)
                        } else {
                            viewModel.updateLocationMethod(option)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // language
            SectionTitle("LANGUAGE")
            SettingsCard {
                SettingsItem(
                    iconRes = R.drawable.languages,
                    title = "Language",
                    subtitle = "Currently: $selectedLanguage",
                    options = listOf("English", "Arabic"),
                    selectedOption = selectedLanguage,
                    onOptionSelected = { viewModel.updateLanguage(it) }
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White.copy(alpha = 0.5f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    @DrawableRes iconRes: Int,
    title: String,
    subtitle: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = Color.Black.copy(alpha = 0.2f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                options.forEach { option ->
                    val isSelected = option == selectedOption
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { onOptionSelected(option) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}