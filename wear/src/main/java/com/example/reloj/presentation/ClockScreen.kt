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
import androidx.compose.ui.draw.drawWithCache
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

// Optimized: DateTimeFormatter is thread-safe and should be defined as a top-level constant
private val TimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val SecondsFormatter = DateTimeFormatter.ofPattern("ss")
private val DateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())
private val AccentBlue = Color(0xFF00D2FF)

// Optimized: Static UI constants defined as top-level properties to avoid overhead of remember
// and redundant allocations during recompositions.
private val BackgroundRingColor = Color.White.copy(alpha = 0.05f)
private val BatteryColor = AccentBlue.copy(alpha = 0.6f)
private val StatBackgroundColor = Color.White.copy(alpha = 0.05f)
private val StatShape = RoundedCornerShape(8.dp)
private val RingOuterStroke = Stroke(width = 8f, cap = StrokeCap.Round)
private val RingInnerStroke = Stroke(width = 4f, cap = StrokeCap.Round)
private val OuterGradient = Brush.sweepGradient(
    0.0f to AccentBlue,
    0.5f to Color(0xFF9D50BB),
    1.0f to AccentBlue
)

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

    // Optimized: Use derivedStateOf for formatted strings to minimize redundant work
    // and prevent unnecessary UI recompositions of the Text components
    val formattedTime by remember { derivedStateOf { currentTime.format(TimeFormatter) } }
    val formattedDate by remember { derivedStateOf { currentTime.format(DateFormatter).uppercase() } }

    val display1Style = MaterialTheme.typography.display1
    val mainTimeStyle = remember(display1Style) {
        display1Style.copy(
            fontSize = 54.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            shadow = Shadow(
                color = AccentBlue.copy(alpha = 0.5f),
                blurRadius = 20f
            )
        )
    }

    val caption2Style = MaterialTheme.typography.caption2
    val dateStyle = remember(caption2Style) {
        caption2Style.copy(
            color = Color(0xFFAAAAAA),
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Light
        )
    }

    val caption1Style = MaterialTheme.typography.caption1
    val secondsStyle = remember(caption1Style) {
        caption1Style.copy(
            color = AccentBlue,
            fontWeight = FontWeight.Medium
        )
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
            Text(
                text = formattedDate,
                style = dateStyle
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main Time
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = formattedTime,
                    style = mainTimeStyle
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                // Seconds (Optimized: Defer state reading to avoid recomposing ClockScreen every second)
                SecondsText(
                    timeProvider = { currentTime },
                    style = secondsStyle
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Health/Stats Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(label = "8.2k", subLabel = "STEPS", color = AccentBlue)
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
private fun SecondsText(timeProvider: () -> LocalTime, style: androidx.compose.ui.text.TextStyle) {
    val seconds = timeProvider().format(SecondsFormatter)
    Text(
        text = seconds,
        modifier = Modifier.padding(bottom = 12.dp),
        style = style
    )
}

@Composable
fun AmbientGlow() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val alphaState = infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Optimized: Use graphicsLayer to apply alpha and drawWithCache to memoize the brush.
    // This avoids 60fps recompositions and redundant object allocations.
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = alphaState.value }
            .drawWithCache {
                val brush = Brush.radialGradient(
                    colors = listOf(AccentBlue, Color.Transparent),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.minDimension / 1.5f
                )
                onDrawBehind {
                    drawCircle(
                        brush = brush,
                        radius = size.minDimension / 1.5f,
                        center = center
                    )
                }
            }
    )
}

@Composable
fun ClockProgressRings() {
    Canvas(modifier = Modifier.size(200.dp).padding(10.dp)) {
        val innerPadding = 12f
        
        // Outer Ring (Steps progress simulation)
        drawArc(
            color = BackgroundRingColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = RingOuterStroke
        )
        drawArc(
            brush = OuterGradient,
            startAngle = -90f,
            sweepAngle = 280f,
            useCenter = false,
            style = RingOuterStroke
        )

        // Inner Ring (Battery simulation)
        val innerSize = size.copy(
            width = size.width - (innerPadding * 2),
            height = size.height - (innerPadding * 2)
        )
        val innerTopLeft = Offset(innerPadding, innerPadding)

        drawArc(
            color = BackgroundRingColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = RingInnerStroke,
            size = innerSize,
            topLeft = innerTopLeft
        )
        drawArc(
            color = BatteryColor,
            startAngle = -90f,
            sweepAngle = 210f,
            useCenter = false,
            style = RingInnerStroke,
            size = innerSize,
            topLeft = innerTopLeft
        )
    }
}

@Composable
fun StatItem(label: String, subLabel: String, color: Color) {

    val caption1 = MaterialTheme.typography.caption1
    val labelStyle = remember(caption1, color) {
        caption1.copy(
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp
        )
    }

    val caption2 = MaterialTheme.typography.caption2
    val subLabelStyle = remember(caption2) {
        caption2.copy(
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(
            color = StatBackgroundColor,
            shape = StatShape
        ).padding(horizontal = 8.dp, vertical = 4.dp)
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

