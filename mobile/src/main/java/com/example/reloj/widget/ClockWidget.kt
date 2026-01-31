package com.example.reloj.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ClockWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val time = LocalTime.now()
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFF0F0C29))
                    .padding(12.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text(
                    text = time.format(timeFormatter),
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "RELOJ GALÁCTICO",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF00D2FF)),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                
                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Box(
                        modifier = GlanceModifier
                            .height(2.dp)
                            .width(40.dp)
                            .background(Color(0xFF00D2FF))
                    ) {}
                }
            }
        }
    }
}
