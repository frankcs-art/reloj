package com.example.reloj.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.geometry.Offset
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.reloj.R
import com.example.reloj.presentation.theme.RelojTheme
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

// Performance optimization: Define formatters as top-level constants to avoid repeated instantiation
// DateTimeFormatter is thread-safe and can be reused.
private val TimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val SecondsFormatter = DateTimeFormatter.ofPattern("ss")
private val DateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")

@Composable
fun ClockScreen() {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000)
        }
    }

    // Performance optimization: Use derivedStateOf to minimize recompositions.
    // timeText and dateText will only trigger recomposition when they actually change (every minute/day).
    // This prevents the entire UI from re-rendering when only the seconds update.
    val dateText by remember {
        derivedStateOf { currentTime.format(DateFormatter).uppercase() }
    }
    val timeText by remember {
        derivedStateOf { currentTime.format(TimeFormatter) }
    }
    val secondsText by remember {
        derivedStateOf { currentTime.format(SecondsFormatter) }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.watch_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        // Background decorative circles
        AmbientGlow()

        // Circular progress indicators (simulating steps/battery)
        ClockProgressRings()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Date
            val baseDateStyle = MaterialTheme.typography.caption2
            val dateStyle = remember(baseDateStyle) {
                baseDateStyle.copy(
                    color = Color(0xFFAAAAAA),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Light
                )
            }
            Text(
                text = dateText,
                style = dateStyle
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main Time
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                // Performance optimization: Memoize TextStyle to avoid repeated allocations
                val baseTimeStyle = MaterialTheme.typography.display1
                val timeTextStyle = remember(baseTimeStyle) {
                    baseTimeStyle.copy(
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color(0xFF00D2FF).copy(alpha = 0.5f),
                            blurRadius = 20f
                        )
                    )
                }

                Text(
                    text = timeText,
                    style = timeTextStyle
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                // Seconds
                val baseSecondsStyle = MaterialTheme.typography.caption1
                val secondsStyle = remember(baseSecondsStyle) {
                    baseSecondsStyle.copy(
                        color = Color(0xFF00D2FF),
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = secondsText,
                    modifier = Modifier.padding(bottom = 12.dp),
                    style = secondsStyle
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Health/Stats Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(label = "8.2k", subLabel = "STEPS", color = Color(0xFF00D2FF))
                StatItem(label = "85", subLabel = "BPM", color = Color(0xFFFF2D55))
            }
        }

        // Wear OS TimeText (Standard system element)
        TimeText(
            timeSource = TimeTextDefaults.timeSource(
                timeFormat = "HH:mm"
            )
        )
    }
}

@Composable
fun AmbientGlow() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF00D2FF).copy(alpha = alpha), Color.Transparent),
                center = center,
                radius = size.minDimension / 1.5f
            ),
            radius = size.minDimension / 1.5f,
            center = center
        )
    }
}

@Composable
fun ClockProgressRings() {
    // Performance optimization: Memoize Brush and constant values
    val sweepBrush = remember {
        Brush.sweepGradient(
            0.0f to Color(0xFF00D2FF),
            0.5f to Color(0xFF9D50BB),
            1.0f to Color(0xFF00D2FF)
        )
    }
    val inactiveColor = remember { Color.White.copy(alpha = 0.05f) }
    val batteryColor = remember { Color(0xFF00D2FF).copy(alpha = 0.6f) }

    Canvas(modifier = Modifier.size(200.dp).padding(10.dp)) {
        val strokeWidth = 8f
        val innerPadding = 12f
        
        // Outer Ring (Steps progress simulation)
        drawArc(
            color = inactiveColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            brush = sweepBrush,
            startAngle = -90f,
            sweepAngle = 280f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Inner Ring (Battery simulation)
        val innerSize = size.copy(
            width = size.width - (innerPadding * 2),
            height = size.height - (innerPadding * 2)
        )
        val innerTopLeft = Offset(innerPadding, innerPadding)

        drawArc(
            color = inactiveColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 4f, cap = StrokeCap.Round),
            size = innerSize,
            topLeft = innerTopLeft
        )
        drawArc(
            color = batteryColor,
            startAngle = -90f,
            sweepAngle = 210f,
            useCenter = false,
            style = Stroke(width = 4f, cap = StrokeCap.Round),
            size = innerSize,
            topLeft = innerTopLeft
        )
    }
}

@Composable
fun StatItem(label: String, subLabel: String, color: Color) {
    // Performance optimization: Memoize Modifier and Shape to avoid repeated allocations
    val itemModifier = remember {
        Modifier.background(
            color = Color.White.copy(alpha = 0.05f),
            shape = RoundedCornerShape(8.dp)
        ).padding(horizontal = 8.dp, vertical = 4.dp)
    }

    val baseStyle1 = MaterialTheme.typography.caption1
    val baseStyle2 = MaterialTheme.typography.caption2

    val labelStyle = remember(baseStyle1, color) {
        baseStyle1.copy(
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp
        )
    }
    val subLabelStyle = remember(baseStyle2) {
        baseStyle2.copy(
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = itemModifier
    ) {
        Text(
            text = label,
            style = labelStyle
        )
        Text(
            text = subLabel,
            style = subLabelStyle
        )
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ClockScreenPreview() {
    RelojTheme {
        ClockScreen()
    }
}
