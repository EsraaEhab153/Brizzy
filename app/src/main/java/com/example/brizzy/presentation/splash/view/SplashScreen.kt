package com.example.brizzy.presentation.splash.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brizzy.presentation.splash.view.ui.theme.BrizzyTheme
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.getValue
import com.example.brizzy.R

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )

        delay(2500)

        /* navController.navigate("home_screen") {
            popUpTo("splash_screen") { inclusive = true }
        }
        */
    }

    val darkBlueTop = Color(0xFF0A1128)
    val lightBlueBottom = Color(0xFF1E3A8A)
    val cardColor = Color(0xFF2A4374)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(darkBlueTop, lightBlueBottom)
                )
            )
    ) {
        StarryBackground()
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .scale(scale.value)
                .alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(32.dp),
                        spotColor = Color(0xFF000000),
                        ambientColor = Color.Black
                    )
                    .background(
                        color = cardColor,
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cloudy_day))

                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )
                
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(130.dp).align(alignment = Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Brizzy",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "WEATHER FORECAST",
                color = Color(0xFFA0B0D0),
                fontSize = 12.sp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "loading...",
                color = Color(0xFFA0B0D0),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun StarryBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val random = Random(42)

        for (i in 1..50) {
            val x = random.nextFloat() * canvasWidth
            val y = random.nextFloat() * canvasHeight
            val radius = random.nextFloat() * 2f + 1f

            drawCircle(
                color = Color.White.copy(alpha = random.nextFloat() * 0.5f + 0.1f),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BrizzyTheme {
        SplashScreen(navController = NavController(context = LocalContext.current))
    }
}