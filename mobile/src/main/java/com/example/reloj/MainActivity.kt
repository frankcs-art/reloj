package com.example.reloj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Optimized: DateTimeFormatter is thread-safe and should be defined as a top-level constant
// to avoid redundant allocations during recompositions.
private val TimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

// Optimized: Background constants defined as top-level properties to avoid
// redundant allocations of Brush and List objects during recompositions.
private val BackgroundColors = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val BackgroundBrush = Brush.verticalGradient(colors = BackgroundColors)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                MobileClockPreview()
            }
        }
    }
}

@Composable
fun MobileClockPreview() {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    // Optimized: Use derivedStateOf for the formatted time string.
    // This ensures that MobileClockPreview only recomposes when the minute changes (HH:mm),
    // instead of every second (1Hz), significantly reducing CPU usage and battery drain.
    val formattedTime by remember {
        derivedStateOf { currentTime.format(TimeFormatter) }
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(32.dp)
        ) {
            Text(
                text = "VISTA PREVIA WIDGET",
                color = Color(0xFF00D2FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = formattedTime,
                color = Color.White,
                fontSize = 80.sp,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(2.dp)
                    .background(Color(0xFF00D2FF))
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Añade el widget a tu pantalla de inicio para la experiencia completa.",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}