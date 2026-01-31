package com.example.reloj.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class ClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = ClockWidget()
}
