package com.dailydashboard.app.widgetgrid

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dailydashboard.core.ui.widgets.GaugeCard
import java.time.LocalTime
import kotlinx.coroutines.delay

/** Batterijniveau en dagvoortgang komen rechtstreeks van het OS/de klok — geen backend nodig. */
@Composable
fun BatteryGaugeWidget(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var level by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val rawLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            level = if (rawLevel >= 0 && scale > 0) rawLevel.toFloat() / scale.toFloat() else 0f
            delay(30_000)
        }
    }

    GaugeCard(
        title = "Batterij",
        progress = level,
        icon = Icons.Filled.BatteryFull,
        modifier = modifier,
    )
}

@Composable
fun DayProgressGaugeWidget(modifier: Modifier = Modifier) {
    var progress by remember { mutableFloatStateOf(dayProgressNow()) }

    LaunchedEffect(Unit) {
        while (true) {
            progress = dayProgressNow()
            delay(60_000)
        }
    }

    GaugeCard(
        title = "Dagvoortgang",
        progress = progress,
        icon = Icons.Filled.Schedule,
        modifier = modifier,
    )
}

private fun dayProgressNow(): Float {
    val now = LocalTime.now()
    return now.toSecondOfDay() / 86_400f
}
